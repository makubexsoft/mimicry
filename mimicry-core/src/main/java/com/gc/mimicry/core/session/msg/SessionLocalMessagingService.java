package com.gc.mimicry.core.session.msg;

import java.util.UUID;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.google.common.base.Preconditions;

public class SessionLocalMessagingService implements MessagingSystem
{
	public SessionLocalMessagingService(UUID sessionId, MessagingSystem messaging)
	{
		Preconditions.checkNotNull( sessionId );
		Preconditions.checkNotNull( messaging );
		this.sessionId = sessionId;
		this.messaging = messaging;
	}

	@Override
	public SessionLocalTopic lookupTopic( String name )
	{
		String sessionTopic = sessionId.toString() + "." + name;
		return new SessionLocalTopic( messaging.lookupTopic( sessionTopic ), name, sessionId );
	}

	@Override
	public TopicSession createTopicSession( Topic topic )
	{
		return messaging.createTopicSession( topic );
	}

	@Override
	public void close()
	{
		messaging.close();
	}

	private final UUID				sessionId;
	private final MessagingSystem	messaging;
}
