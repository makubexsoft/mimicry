package org.mimicry.tests;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mimicry.Application;
import org.mimicry.Node;
import org.mimicry.NodeParameters;
import org.mimicry.cep.EventFuture;
import org.mimicry.cep.Query;
import org.mimicry.engine.EventHandlerParameters;
import org.mimicry.junit.MimicryTestCase;
import org.mimicry.junit.SimulationConfiguration;
import org.mimicry.streams.StdOutStream;
import org.mimicry.timing.TimelineType;
import org.mimicry.util.concurrent.Future;


public class TestBundle1 extends MimicryTestCase
{
	@Test
	@SimulationConfiguration(timeline = TimelineType.DISCRETE)
	public void testCanStopInfiniteLoop() throws InterruptedException
	{
		NodeParameters nodeParams = new NodeParameters( "myNode" );
		nodeParams.getEventStack().add( new EventHandlerParameters( "org.mimicry.handler.StdIOStreamingHandler" ) );
		Node node = getSimulation().createNode( nodeParams );
		Application application = node.installApplication( "bundle1", "/apps/bundle1" );

		// Start application and wait until the loop has been entered
		StdOutStream.get( getEventEngine() );
		Query query = getEventEngine().addQuery( "from StdOut[text == 'looping...'] return" );
		EventFuture eventFuture = new EventFuture( query );
		application.start();
		eventFuture.await( 5,  TimeUnit.SECONDS );
		assertTrue(eventFuture.isSuccess());
		
		// Force termination
		Future<?> future = application.stop();
		future.await( 5, TimeUnit.SECONDS );
		assertTrue(future.isSuccess());
	}
}
