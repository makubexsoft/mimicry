package com.gc.mimicry.core.session.engine;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.LocalNode;
import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageReceiver;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Subscriber;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.gc.mimicry.core.session.QuerySessionRequest;
import com.gc.mimicry.core.session.QuerySessionResponse;
import com.gc.mimicry.core.session.SessionInfo;
import com.gc.mimicry.core.session.keepalive.KeepAliveDriver;
import com.gc.mimicry.core.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.net.NodeInfo;
import com.google.common.base.Preconditions;

public class SessionDriver extends BaseResourceManager implements MessageReceiver
{
    public SessionDriver(UUID sessionId, TopicSession managementSession, LocalNode localNode,
            Set<NodeInfo> participants, MessagingSystem messaging)
    {
        Preconditions.checkNotNull(sessionId);
        Preconditions.checkNotNull(managementSession);
        Preconditions.checkNotNull(localNode);
        Preconditions.checkNotNull(participants);
        Preconditions.checkNotNull(messaging);

        this.sessionId = sessionId;
        this.participants = participants;

        publisher = managementSession.createPublisher();
        attachResource(publisher);

        subscriber = managementSession.createSubscriber();
        attachResource(subscriber);
        subscriber.setMessageReceiver(this);

        localMessaging = new SessionLocalMessagingService(sessionId, messaging);

        attachResource(new LogicalNodeManager(localMessaging));
        attachResource(new KeepAliveDriver(localMessaging, localNode.getNodeInfo().getNodeId()));
    }

    @Override
    public void messageReceived(Topic topic, Message msg)
    {
        if (msg instanceof QuerySessionRequest)
        {
            handleSessionQuery((QuerySessionRequest) msg);
        }
        // TODO: listen for session close requests
    }

    private void handleSessionQuery(QuerySessionRequest request)
    {
        QuerySessionResponse response;
        response = new QuerySessionResponse(request.getRequestId(), new SessionInfo(sessionId, participants));
        publisher.send(response);
    }

    private final UUID sessionId;
    private final Set<NodeInfo> participants;
    private final Publisher publisher;
    private final Subscriber subscriber;
    private final SessionLocalMessagingService localMessaging;
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(SessionDriver.class);
    }
}
