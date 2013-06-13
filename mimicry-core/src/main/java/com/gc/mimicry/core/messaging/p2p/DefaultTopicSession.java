package com.gc.mimicry.core.messaging.p2p;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Subscriber;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;

public class DefaultTopicSession implements TopicSession
{

	private final DefaultMessagingSystem					system;
	private final CopyOnWriteArrayList<DefaultSubscriber>	subscribers;
	private final Topic										topic;
	private final Set<Closeable>							participants;
	private final Set<Message>								messagesToSuppres;

	DefaultTopicSession(DefaultMessagingSystem system, Topic topic)
	{
		this.system = system;
		this.topic = topic;
		participants = new CopyOnWriteArraySet<Closeable>();
		messagesToSuppres = new HashSet<Message>();
		subscribers = new CopyOnWriteArrayList<DefaultSubscriber>();
	}

	@Override
	public Topic getTopic()
	{
		return topic;
	}

	void deliver( Message msg )
	{
		if ( !messagesToSuppres.remove( msg ) )
		{
			for ( DefaultSubscriber s : subscribers )
			{
				s.deliver( msg );
			}
		}
	}

	void broadcast( Message msg )
	{
		messagesToSuppres.add( msg );
		system.broadcast( getTopic(), msg );
	}

	void send( Message msg, UUID destinationNode )
	{
		messagesToSuppres.add( msg );
		system.send( getTopic(), msg, destinationNode );
	}

	void unsubscribe( DefaultSubscriber subscriber )
	{
		subscribers.remove( subscriber );
		participants.remove( subscriber );
	}

	@Override
	public Publisher createPublisher()
	{
		DefaultPublisher publisher = new DefaultPublisher( this );
		participants.add( publisher );
		return publisher;
	}

	@Override
	public Subscriber createSubscriber()
	{
		DefaultSubscriber subscriber = new DefaultSubscriber( this, getTopic() );
		subscribers.add( subscriber );
		participants.add( subscriber );
		return subscriber;
	}

	@Override
	public void close()
	{
		system.sessionClosed( this );
		for ( Closeable c : participants )
		{
			try
			{
				c.close();
			}
			catch ( IOException e )
			{
				// ignore
			}
		}
	}
}
