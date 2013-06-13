package com.gc.mimicry.core.messaging.p2p;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Topic;

public class DefaultPublisher implements Publisher
{

	private DefaultTopicSession	session;
	private UUID				destinationNode;

	DefaultPublisher(DefaultTopicSession session)
	{
		this.session = session;
		destinationNode = getDestinationNode();
	}

	public Topic getTopic()
	{
		return session.getTopic();
	}

	public void send( Message msg )
	{
		if ( destinationNode == null )
		{
			session.broadcast( msg );
		}
		else
		{
			session.send( msg, destinationNode );
		}
	}

	private UUID getDestinationNode()
	{
		String topicName = getTopic().getName();
		int index = topicName.indexOf( "/" );
		if ( index <= 0 )
		{
			return null;
		}
		else
		{
			String id = topicName.substring( 0, index );
			return UUID.fromString( id );
		}
	}

	public void close()
	{
	}
}
