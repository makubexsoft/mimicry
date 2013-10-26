package org.mimicry.tests;

import org.junit.Test;
import org.mimicry.cep.Event;
import org.mimicry.cep.StreamListener;
import org.mimicry.engine.Application;
import org.mimicry.engine.Node;
import org.mimicry.engine.NodeParameters;
import org.mimicry.engine.stack.EventHandlerParameters;
import org.mimicry.engine.streams.StdOutStream;
import org.mimicry.engine.timing.TimelineType;
import org.mimicry.junit.MimicryTestCase;
import org.mimicry.junit.SimulationConfiguration;

public class TestBundle3 extends MimicryTestCase
{
	@Test
	@SimulationConfiguration(timeline = TimelineType.DISCRETE)
	public void test() throws InterruptedException
	{
		NodeParameters nodeParams = new NodeParameters( "myNode" );
		nodeParams.getEventStack().add( new EventHandlerParameters( "org.mimicry.handler.StdIOStreamingHandler" ) );
		Node node = getSimulation().createNode( nodeParams );
		
		Application application = node.installApplication( "bundle3", "/apps/bundle3" );

		StdOutStream.get( getEventEngine() ).addStreamListener( new StreamListener()
		{
			@Override
			public void receive( Event[] events )
			{
				System.out.println(events[0].getField( 2 ));
			}
		} );
		
		application.start();
		Thread.sleep( 10000 );
	}
}
