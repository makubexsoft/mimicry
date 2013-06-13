package com.gc.mimicry.core.runtime;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.aspectj.weaver.tools.WeavingAdaptor;
import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.deployment.ApplicationDescriptorBuilder;
import com.gc.mimicry.core.deployment.LocalApplicationRepository;
import com.gc.mimicry.core.messaging.p2p.DefaultMessagingSystem;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.net.InMemoryNetwork;
import com.gc.mimicry.util.concurrent.Future;

public class TestApplicationManager
{
	private InMemoryNetwork				network;
	private DefaultMessagingSystem		messaging;
	private Node						node;
	private LocalApplicationRepository	appRepo;

	@Before
	public void setUp() throws MalformedURLException
	{
		System.setProperty( "org.aspectj.tracing.debug", "true" );
		System.setProperty( "org.aspectj.tracing.enabled", "true" );
		System.setProperty( "org.aspectj.tracing.messages", "true" );
		System.setProperty( "aj.weaving.verbose", "true" );
		// System.setProperty( "org.aspectj.weaver.showWeaveInfo", "true" );
		// System.setProperty( "org.aspectj.weaving.messages", "true" );
		System.setProperty( WeavingAdaptor.SHOW_WEAVE_INFO_PROPERTY, "true" );
		System.setProperty( WeavingAdaptor.TRACE_MESSAGES_PROPERTY, "true" );
		// System.setProperty( WeavingAdaptor.WEAVING_ADAPTOR_VERBOSE, "true" );
		//
		// System.setProperty( WeavingAdaptor.SHOW_WEAVE_INFO_PROPERTY, "true"
		// );
		// System.setProperty( WeavingAdaptor.WEAVING_ADAPTOR_VERBOSE, "true" );
		// System.setProperty( WeavingAdaptor.TRACE_MESSAGES_PROPERTY, "true" );
		// System.setProperty( "org.aspectj.tracing.enabled", "true" );
		System.setProperty( "org.aspectj.tracing.factory", "default" );

		ClassLoadingContext ctx = new ClassLoadingContext( new URLClassLoader( new URL[0], getClass().getClassLoader() ) );
		ctx.addAspectClassPath( new File( "../mimicry-aspects/target/classes" ).toURI().toURL() );

		ctx.addBridgeClassPath( new File( "../mimicry-aspects/target/classes" ).toURI().toURL() );
		ctx.addBridgeClassPath( new File( "../mimicry-bridge/target/classes" ).toURI().toURL() );

		network = new InMemoryNetwork();
		messaging = new DefaultMessagingSystem( network.createNode() );
		node = new Node( ctx, "test-node", messaging );
		appRepo = new LocalApplicationRepository( new File( "src/test/resources" ) );
	}

	@Test
	public void testLaunchApplication() throws IOException, InterruptedException
	{
		ApplicationDescriptor appDesc = appRepo.getApplicationDescriptor( "sample-app" );
		Application application = node.getApplicationManager().launchApplication( appDesc );

		//
		application.start();

		Thread.sleep( 2000 );

		Future<?> future = application.stop();

		future.await( 5000 );
		assertTrue( future.isSuccess() );

		assertNotNull( application );
	}

	@Test
	public void testLaunch2ApplicationInstances() throws IOException, InterruptedException
	{
		ApplicationDescriptor appDesc = appRepo.getApplicationDescriptor( "sample-app" );
		Application app1 = node.getApplicationManager().launchApplication( appDesc );
		Application app2 = node.getApplicationManager().launchApplication( appDesc );

		//
		app1.start();
		app2.start();

		((RealtimeClock) app1.getClock()).start( 1.0 );
		((RealtimeClock) app2.getClock()).start( 1.0 );

		Thread.sleep( 5000 );

		stopAndAssertTermination( app1 );
		stopAndAssertTermination( app2 );
	}

	private void stopAndAssertTermination( Application app1 ) throws InterruptedException
	{
		app1.stop();
		Future<?> future1 = app1.getTerminationFuture();
		future1.await( 5000 );
		assertTrue( future1.isSuccess() );
	}

	@Test
	public void testPingPong() throws IOException, InterruptedException
	{
		ApplicationDescriptorBuilder clientBuilder = ApplicationDescriptorBuilder.newDescriptor( "client" );
		clientBuilder.withMainClass( "examples.PingPongClient" );
		clientBuilder.withCommandLine( "127.0.0.1 8000" );
		clientBuilder.withRunnableJar( "sample-app.jar" );
		clientBuilder.withClassPath( "sample-app.jar" );
		ApplicationDescriptor clientDesc = clientBuilder.build();

		ApplicationDescriptorBuilder serverBuilder = ApplicationDescriptorBuilder.newDescriptor( "server" );
		serverBuilder.withMainClass( "examples.PingPongServer" );
		serverBuilder.withCommandLine( "8000" );
		serverBuilder.withRunnableJar( "sample-app.jar" );
		serverBuilder.withClassPath( "sample-app.jar" );
		ApplicationDescriptor serverDesc = serverBuilder.build();

		Application client = node.getApplicationManager().launchApplication( clientDesc );
		Application server = node.getApplicationManager().launchApplication( serverDesc );

		((RealtimeClock) server.getClock()).start( 1.0 );
		((RealtimeClock) client.getClock()).start( 0.5 );

		//
		server.start();
		client.start();

		Thread.sleep( 5000 );

		stopAndAssertTermination( client );
		stopAndAssertTermination( server );
	}
}
