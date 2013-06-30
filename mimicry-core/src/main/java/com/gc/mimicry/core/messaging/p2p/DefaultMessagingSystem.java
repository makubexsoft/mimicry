package com.gc.mimicry.core.messaging.p2p;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.gc.mimicry.net.ClusterListener;
import com.gc.mimicry.net.ClusterNode;
import com.gc.mimicry.net.NodeInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DefaultMessagingSystem implements MessagingSystem, ClusterListener
{

    private final Multimap<String, DefaultTopicSession> sessions;
    private final ClusterNode node;

    public DefaultMessagingSystem(ClusterNode node)
    {
        Preconditions.checkNotNull(node);
        this.node = node;
        sessions = HashMultimap.create();
        node.addClusterListener(this);
    }

    public Topic lookupTopic(String name)
    {
        return new DefaultTopic(name);
    }

    public TopicSession createTopicSession(Topic topic)
    {
        DefaultTopicSession session = new DefaultTopicSession(this, topic);
        synchronized (sessions)
        {
            sessions.put(topic.getName(), session);
        }
        return session;
    }

    void sessionClosed(TopicSession session)
    {
        synchronized (sessions)
        {
            sessions.get(session.getTopic().getName()).remove(session);
        }
    }

    public void nodeJoined(NodeInfo node)
    {
    }

    public void nodeLeft(NodeInfo node)
    {
    }

    void broadcast(Topic topic, Message msg)
    {
        Message env = new MessageEnvelope(msg, topic.getName());
        node.broadcast(env);
        deliver(msg, topic.getName());
    }

    void send(Topic topic, Message msg, UUID destinationNode)
    {
        Message env = new MessageEnvelope(msg, topic.getName());
        node.send(env, destinationNode);
        deliver(msg, topic.getName());
    }

    public void messageReceived(Message msg, NodeInfo sender)
    {
        MessageEnvelope envelope = (MessageEnvelope) msg;
        Message content = envelope.getContent();
        String topicName = envelope.getTopicName();

        deliver(content, topicName);
    }

    private void deliver(Message content, String topicName)
    {
        synchronized (sessions)
        {
            Collection<DefaultTopicSession> matchingSessions = new ArrayList<DefaultTopicSession>(
                    sessions.get(topicName));
            for (DefaultTopicSession session : matchingSessions)
            {
                session.deliver(content);
            }
        }
    }

    public void close()
    {
        node.removeClusterListener(this);
        synchronized (sessions)
        {
            List<DefaultTopicSession> tmp = new ArrayList<DefaultTopicSession>(sessions.values());
            for (DefaultTopicSession session : tmp)
            {
                session.close();
            }
        }
    }
}
