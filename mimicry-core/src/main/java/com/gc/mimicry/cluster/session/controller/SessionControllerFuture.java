package com.gc.mimicry.cluster.session.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.cluster.NodeInfo;
import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.cluster.messaging.MessageReceiver;
import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.gc.mimicry.cluster.messaging.Publisher;
import com.gc.mimicry.cluster.messaging.Subscriber;
import com.gc.mimicry.cluster.messaging.Topic;
import com.gc.mimicry.cluster.messaging.TopicSession;
import com.gc.mimicry.cluster.session.ParticipateInSessionMessage;
import com.gc.mimicry.cluster.session.SessionCreatedMessage;
import com.gc.mimicry.util.concurrent.DefaultValueFuture;

public class SessionControllerFuture extends DefaultValueFuture<SessionController> implements MessageReceiver
{

    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(SessionControllerFuture.class);
    }
    private Set<NodeInfo> participatingNodes;
    private UUID sessionId;
    private volatile int requiredNodes;
    private Subscriber subscriber;
    private Publisher publisher;
    private MessagingSystem messaging;

    SessionControllerFuture(UUID sessionId, int requiredNodes, TopicSession topicSession, MessagingSystem messaging)
    {
        this.sessionId = sessionId;
        this.requiredNodes = requiredNodes;
        this.subscriber = topicSession.createSubscriber();
        this.publisher = topicSession.createPublisher();
        this.messaging = messaging;
        participatingNodes = new HashSet<NodeInfo>();
        subscriber.setMessageReceiver(this);
    }

    @Override
    protected boolean performCancellation()
    {
        subscriber.setMessageReceiver(null);
        subscriber.close();
        publisher.close();
        return true;
    }

    public void messageReceived(Topic topic, Message msg)
    {
        if (msg instanceof ParticipateInSessionMessage)
        {
            ParticipateInSessionMessage participateMsg = (ParticipateInSessionMessage) msg;

            if (sessionId.equals(participateMsg.getSessionId()))
            {
                addNode(participateMsg);
            }
        }
    }

    private void addNode(ParticipateInSessionMessage participateMsg)
    {
        logger.info("Node[" + participateMsg.getNode().getNodeId() + "] participating in Session["
                + participateMsg.getSessionId() + "]");
        participatingNodes.add(participateMsg.getNode());

        checkWhetherHaveEnoughParticipants();
    }

    private void checkWhetherHaveEnoughParticipants()
    {
        if (participatingNodes.size() >= requiredNodes)
        {
            SessionController controller = createController();
            publisher.send(new SessionCreatedMessage(sessionId, participatingNodes));
            setValue(controller);
        }
    }

    public Set<NodeInfo> getCurrentNodes()
    {
        return participatingNodes;
    }

    public void setRequiredNodes(int requiredNodes)
    {
        this.requiredNodes = requiredNodes;
        checkWhetherHaveEnoughParticipants();
    }

    public int getRequiredNodes()
    {
        return requiredNodes;
    }

    private SessionController createController()
    {
        logger.info("Found " + participatingNodes.size() + " node(s) participating in session " + sessionId
                + ". Creating session controller.");
        return new SessionController(sessionId, participatingNodes, messaging);
    }
}
