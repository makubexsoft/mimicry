package com.gc.mimicry.core.messaging.p2p;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageReceiver;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.gc.mimicry.net.InMemoryNetwork;
import com.gc.mimicry.net.InMemoryNetwork.VirtualNode;

public class TestDefaultMessagingSystem
{
	private InMemoryNetwork			network;
	private VirtualNode				node;
	private DefaultMessagingSystem	system;

	@Before
	public void setUp()
	{
		network = new InMemoryNetwork();
		node = network.createNode();
		system = new DefaultMessagingSystem( node );
	}

	@After
	public void tearDown()
	{
		system.close();
		node.close();
	}

	@Test
	public void testLocalMessagePassing()
	{
		Topic topic = system.lookupTopic( "localTopic" );
		TopicSession sessionA = system.createTopicSession( topic );
		TopicSession sessionB = system.createTopicSession( topic );

		Message someMessage = Mockito.mock( Message.class );
		MessageReceiver receiver = Mockito.mock( MessageReceiver.class );
		sessionB.createSubscriber().setMessageReceiver( receiver );
		sessionA.createPublisher().send( someMessage );

		Mockito.verify( receiver, Mockito.timeout( 500 ) ).messageReceived( topic, someMessage );
	}

	@Test
	public void testNoLoopbackWithinSession() throws InterruptedException
	{
		Topic topic = system.lookupTopic( "localTopic" );
		TopicSession session = system.createTopicSession( topic );

		Message someMessage = Mockito.mock( Message.class );
		MessageReceiver receiver = Mockito.mock( MessageReceiver.class );
		session.createSubscriber().setMessageReceiver( receiver );
		session.createPublisher().send( someMessage );

		Thread.sleep( 500 );
		Mockito.verifyZeroInteractions( receiver );
	}
}
