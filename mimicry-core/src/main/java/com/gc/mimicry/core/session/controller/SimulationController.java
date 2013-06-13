package com.gc.mimicry.core.session.controller;

import java.util.UUID;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.gc.mimicry.core.session.CreateSessionMessage;
import com.gc.mimicry.core.session.SessionConstants;
import com.google.common.base.Preconditions;

public class SimulationController
{
	public SimulationController(MessagingSystem messaging)
	{
		Preconditions.checkNotNull( messaging );

		this.messaging = messaging;

		sessionBrowser = new SessionBrowser( messaging );

		Topic managementTopic = messaging.lookupTopic( SessionConstants.SESSION_MGNT_TOPIC_NAME );
		topicSession = messaging.createTopicSession( managementTopic );
		publisher = topicSession.createPublisher();
	}

	public SessionControllerFuture createSession( int requiredNodes )
	{
		UUID sessionId = UUID.randomUUID();
		SessionControllerFuture future = new SessionControllerFuture( sessionId, requiredNodes, topicSession, messaging );
		publisher.send( new CreateSessionMessage( sessionId ) );
		return future;
	}

	public SessionBrowser getSessionBrowser()
	{
		return sessionBrowser;
	}

	private final TopicSession		topicSession;
	private final Publisher			publisher;
	private final MessagingSystem	messaging;
	private final SessionBrowser	sessionBrowser;
}
