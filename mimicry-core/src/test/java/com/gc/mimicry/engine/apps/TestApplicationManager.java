package com.gc.mimicry.engine.apps;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.aspectj.weaver.tools.WeavingAdaptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.gc.mimicry.engine.ClassLoadingContext;
import com.gc.mimicry.engine.apps.Application;
import com.gc.mimicry.engine.deployment.ApplicationBundleDescriptor;
import com.gc.mimicry.engine.deployment.ApplicationDescriptorBuilder;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.nodes.Node;
import com.gc.mimicry.engine.timing.RealtimeClock;
import com.gc.mimicry.util.concurrent.Future;

public class TestApplicationManager
{
    private static final String BRIDGE_PATH = "../mimicry-bridge/target/classes";
    private static final String ASPECTS_PATH = "../mimicry-aspects/target/classes";

    private RealtimeClock clock;
    private Node node;
    private LocalApplicationRepository appRepo;

    @Before
    public void setUp() throws IOException
    {
        configureAspectJ();

        ClassLoader classLoader = getClass().getClassLoader();

        URLClassLoader coreClassLoader;
        coreClassLoader = new URLClassLoader(new URL[0], classLoader);

        ClassLoadingContext ctx;
        ctx = new ClassLoadingContext(coreClassLoader);
        ctx.addAspectClassPath(new File(ASPECTS_PATH).toURI().toURL());

        ctx.addBridgeClassPath(new File(ASPECTS_PATH).toURI().toURL());
        ctx.addBridgeClassPath(new File(BRIDGE_PATH).toURI().toURL());

        node = mock(Node.class);
        Mockito.when(node.getClock()).thenReturn(clock);

        appRepo = new LocalApplicationRepository(new File("src/test/resources"));
    }

    private void configureAspectJ()
    {
        System.setProperty("org.aspectj.tracing.debug", "true");
        System.setProperty("org.aspectj.tracing.enabled", "true");
        System.setProperty("org.aspectj.tracing.messages", "true");
        System.setProperty("aj.weaving.verbose", "true");
        System.setProperty(WeavingAdaptor.SHOW_WEAVE_INFO_PROPERTY, "true");
        System.setProperty(WeavingAdaptor.TRACE_MESSAGES_PROPERTY, "true");
        System.setProperty("org.aspectj.tracing.factory", "default");

        System.setProperty("org.aspectj.weaver.loadtime.configuration",
                "/data/projects/Mimicry/mimicry/mimicry-core/META-INF/aop.xml");
    }

    @Test
    public void testLaunchApplication() throws IOException, InterruptedException
    {
        ApplicationBundleDescriptor appDesc;
        Application application;
        appDesc = appRepo.getApplicationDescriptor("sample-app");
        application = node.getApplicationManager().launchApplication(appDesc);

        application.start();

        Thread.sleep(2000);

        Future<?> future = application.stop();

        future.await(5000);
        assertTrue(future.isSuccess());

        assertNotNull(application);
    }

    @Test
    public void testLaunch2ApplicationInstances() throws IOException, InterruptedException
    {
        ApplicationBundleDescriptor appDesc;
        appDesc = appRepo.getApplicationDescriptor("sample-app");

        Application app1;
        Application app2;
        app1 = node.getApplicationManager().launchApplication(appDesc);
        app2 = node.getApplicationManager().launchApplication(appDesc);

        app1.start();
        app2.start();

        clock.start(1.0);

        Thread.sleep(5000);

        stopAndAssertTermination(app1);
        stopAndAssertTermination(app2);
    }

    @Test
    public void testPingPong() throws IOException, InterruptedException
    {
        ApplicationDescriptorBuilder clientBuilder;
        clientBuilder = ApplicationDescriptorBuilder.newDescriptor("client");
        clientBuilder.withMainClass("examples.PingPongClient");
        clientBuilder.withRunnableJar("sample-app.jar");
        clientBuilder.withClassPath("sample-app.jar");
        ApplicationBundleDescriptor clientDesc = clientBuilder.build();

        ApplicationDescriptorBuilder serverBuilder;
        serverBuilder = ApplicationDescriptorBuilder.newDescriptor("server");
        serverBuilder.withMainClass("examples.PingPongServer");
        serverBuilder.withRunnableJar("sample-app.jar");
        serverBuilder.withClassPath("sample-app.jar");
        ApplicationBundleDescriptor serverDesc = serverBuilder.build();

        Application client;
        Application server;
        client = node.getApplicationManager().launchApplication(clientDesc);
        server = node.getApplicationManager().launchApplication(serverDesc);

        clock.start(1.0);

        server.start("8000");
        client.start("127.0.0.1", "8000");

        Thread.sleep(5000);

        stopAndAssertTermination(client);
        stopAndAssertTermination(server);
    }

    private void stopAndAssertTermination(Application app1) throws InterruptedException
    {
        app1.stop();
        Future<?> future1 = app1.getTerminationFuture();
        future1.await(5000);
        assertTrue(future1.isSuccess());
    }
}