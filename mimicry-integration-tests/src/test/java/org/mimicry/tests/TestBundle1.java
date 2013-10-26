package org.mimicry.tests;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mimicry.cep.EventFuture;
import org.mimicry.cep.Query;
import org.mimicry.engine.Application;
import org.mimicry.engine.Node;
import org.mimicry.engine.NodeParameters;
import org.mimicry.engine.streams.StdOutStream;
import org.mimicry.engine.timing.TimelineType;
import org.mimicry.junit.MimicryTestCase;
import org.mimicry.junit.SimulationConfiguration;
import org.mimicry.util.concurrent.Future;


public class TestBundle1 extends MimicryTestCase
{
	@Test
	@SimulationConfiguration(timeline = TimelineType.DISCRETE)
	public void test() throws InterruptedException
	{
		Node node = getSimulation().createNode( new NodeParameters( "myNode" ) );
		Application application = node.installApplication( "bundle1", "/apps/bundle1" );

		// Start application and wait until the loop has been entered
		StdOutStream.get( getEventEngine() );
		Query query = getEventEngine().addQuery( "from StdOut[text == 'looping...'] return" );
		EventFuture eventFuture = new EventFuture( query );
		application.start();
		eventFuture.await( 5,  TimeUnit.SECONDS );
		
		// Force termination
		Future<?> future = application.stop();
		future.await( 5, TimeUnit.SECONDS );
		
		assertTrue(future.isSuccess());
	}
}
