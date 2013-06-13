package com.gc.mimicry.core.session.msg;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Topic;
import com.google.common.base.Preconditions;

public class SessionLocalTopic implements Topic
{
	SessionLocalTopic(Topic topic, String name, UUID sessionId)
	{
		Preconditions.checkNotNull( topic );
		Preconditions.checkNotNull( name );
		Preconditions.checkNotNull( sessionId );
		this.topic = topic;
		this.name = name;
		this.sessionId = sessionId;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public UUID getSessionId()
	{
		return sessionId;
	}

	public Topic getGlobalTopic()
	{
		return topic;
	}

	private final Topic		topic;
	private final String	name;
	private final UUID		sessionId;
}
