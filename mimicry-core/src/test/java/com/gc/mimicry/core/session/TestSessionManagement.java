package com.gc.mimicry.core.session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.p2p.DefaultMessagingSystem;
import com.gc.mimicry.core.session.controller.SessionController;
import com.gc.mimicry.core.session.controller.SimulationController;
import com.gc.mimicry.core.session.engine.SimulationEngine;
import com.gc.mimicry.net.ClusterListener;
import com.gc.mimicry.net.ClusterNode;
import com.gc.mimicry.net.InMemoryNetwork;
import com.gc.mimicry.net.InMemoryNetwork.VirtualNode;
import com.gc.mimicry.util.concurrent.ValueFuture;

public class TestSessionManagement
{

	private static final int	DISCOVERY_TIMEOUT_MILLIS	= 1500;

	private InMemoryNetwork		network;
	private ClusterListener		listener;
	private VirtualNode			nodeA;
	private VirtualNode			nodeB;

	@Before
	public void setUp()
	{
		network = new InMemoryNetwork();
		listener = mock( ClusterListener.class );
		nodeA = network.createNode();
		nodeA.addClusterListener( listener );
		nodeB = network.createNode();
	}

	@After
	public void tearDown()
	{
		nodeB.close();
		nodeA.close();
	}

	@Test
	public void testCreateSession() throws InterruptedException
	{
		MessagingSystem messagingA = new DefaultMessagingSystem( nodeA );
		SimulationEngine engineA = new SimulationEngine( messagingA, nodeA );
		engineA.start();

		MessagingSystem messagingB = new DefaultMessagingSystem( nodeB );
		SimulationController controller = new SimulationController( messagingB );

		verify( listener, timeout( DISCOVERY_TIMEOUT_MILLIS ) ).nodeJoined( eq( nodeB.getNodeInfo() ) );

		ValueFuture<SessionController> future = controller.createSession( 1 );
		future.await( 500 );
		assertTrue( future.isSuccess() );

		messagingB.close();
		messagingA.close();
	}

	@Test
	public void testMissingParticipantsCreateSession() throws InterruptedException
	{
		MessagingSystem messagingA = new DefaultMessagingSystem( nodeA );
		SimulationEngine engineA = new SimulationEngine( messagingA, nodeA );
		engineA.start();

		MessagingSystem messagingB = new DefaultMessagingSystem( nodeB );
		SimulationController controller = new SimulationController( messagingB );

		verify( listener, timeout( DISCOVERY_TIMEOUT_MILLIS ) ).nodeJoined( eq( nodeB.getNodeInfo() ) );

		ValueFuture<SessionController> future = controller.createSession( 2 );
		future.await( 500 );
		assertFalse( future.isSuccess() );

		messagingB.close();
		messagingA.close();
	}

	@Test
	public void testMultipleEnginesCreateSession() throws InterruptedException
	{
		MessagingSystem messagingA = new DefaultMessagingSystem( nodeA );
		SimulationEngine engineA = new SimulationEngine( messagingA, nodeA );
		engineA.start();

		VirtualNode nodeC = network.createNode();
		MessagingSystem messagingC = new DefaultMessagingSystem( nodeC );
		SimulationEngine engineB = new SimulationEngine( messagingC, nodeC );
		engineB.start();

		MessagingSystem messagingB = new DefaultMessagingSystem( nodeB );
		SimulationController controller = new SimulationController( messagingB );

		verify( listener, timeout( DISCOVERY_TIMEOUT_MILLIS ) ).nodeJoined( eq( nodeC.getNodeInfo() ) );

		ValueFuture<SessionController> future = controller.createSession( 2 );
		future.await( 500 );
		assertTrue( future.isSuccess() );

		messagingB.close();
		messagingA.close();
		nodeC.close();
	}

	@Test
	public void testControllerAndEngineOnSameNode() throws InterruptedException
	{
		MessagingSystem messagingA = new DefaultMessagingSystem( nodeA );
		SimulationEngine engineA = new SimulationEngine( messagingA, nodeA );
		engineA.start();

		MessagingSystem messagingB = new DefaultMessagingSystem( nodeB );
		SimulationEngine engineB = new SimulationEngine( messagingB, nodeB );
		engineB.start();

		SimulationController controller = new SimulationController( messagingB );

		verify( listener, timeout( DISCOVERY_TIMEOUT_MILLIS ) ).nodeJoined( eq( nodeB.getNodeInfo() ) );

		ValueFuture<SessionController> future = controller.createSession( 2 );
		future.await( 500 );
		assertTrue( future.isSuccess() );

		messagingB.close();
		messagingA.close();
	}
}
