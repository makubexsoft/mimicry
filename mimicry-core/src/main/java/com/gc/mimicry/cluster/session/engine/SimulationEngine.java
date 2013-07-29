package com.gc.mimicry.cluster.session.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.cluster.LocalNode;
import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.cluster.messaging.MessageReceiver;
import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.gc.mimicry.cluster.messaging.Publisher;
import com.gc.mimicry.cluster.messaging.Subscriber;
import com.gc.mimicry.cluster.messaging.Topic;
import com.gc.mimicry.cluster.messaging.TopicSession;
import com.gc.mimicry.cluster.session.CreateSessionMessage;
import com.gc.mimicry.cluster.session.ParticipateInSessionMessage;
import com.gc.mimicry.cluster.session.SessionConstants;
import com.gc.mimicry.cluster.session.SessionCreatedMessage;
import com.google.common.base.Preconditions;

public class SimulationEngine implements MessageReceiver
{

    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(SimulationEngine.class);
    }

    private final Map<UUID, SessionDriver> sessions;
    private final LocalNode localNode;
    private final MessagingSystem messaging;
    private TopicSession topicSession;
    private Publisher publisher;
    private Subscriber subscriber;
    private Topic managementTopic;

    public SimulationEngine(MessagingSystem messaging, LocalNode localNode)
    {
        Preconditions.checkNotNull(messaging);
        Preconditions.checkNotNull(localNode);

        this.messaging = messaging;
        this.localNode = localNode;
        sessions = new HashMap<UUID, SessionDriver>();
    }

    public void start()
    {
        managementTopic = messaging.lookupTopic(SessionConstants.SESSION_MGNT_TOPIC_NAME);
        topicSession = messaging.createTopicSession(managementTopic);
        publisher = topicSession.createPublisher();
        subscriber = topicSession.createSubscriber();
        subscriber.setMessageReceiver(this);
    }

    public void messageReceived(Topic topic, Message msg)
    {
        Preconditions.checkNotNull(topic);
        Preconditions.checkNotNull(msg);
        if (msg instanceof CreateSessionMessage)
        {
            CreateSessionMessage sessionMsg = (CreateSessionMessage) msg;
            logger.info("Create session request. Node[" + localNode.getNodeInfo().getNodeId()
                    + "] will participate in Session[" + sessionMsg.getSessionId() + "]");
            publisher.send(new ParticipateInSessionMessage(sessionMsg.getSessionId(), localNode.getNodeInfo()));

        }
        else if (msg instanceof SessionCreatedMessage)
        {
            SessionCreatedMessage sessionMsg = (SessionCreatedMessage) msg;

            UUID sessionId = sessionMsg.getSessionId();
            SessionDriver driver = sessions.get(sessionId);

            if (sessionMsg.getParticipants().contains(localNode.getNodeInfo()))
            {
                // we don't allow to preempt sessions to avoid duplicate packets
                // interrupting running sessions
                if (driver != null)
                {
                    return;
                }
                System.out.println("Session[id=" + sessionId + "] has been confirmed. Creating driver...");
                SessionDriver newDriver = new SessionDriver(sessionId, messaging.createTopicSession(managementTopic),
                        localNode, sessionMsg.getParticipants(), messaging);
                sessions.put(sessionId, newDriver);
            }
            else
            {
                if (driver == null)
                {
                    return;
                }
                logger.info("Closing driver of unconfirmed session " + sessionId);
                sessions.remove(sessionId);
                driver.close();
            }
        }
    }
}
