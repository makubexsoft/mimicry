package com.gc.mimicry.core.session.keepalive;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageReceiver;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Publisher;
import com.gc.mimicry.core.messaging.Subscriber;
import com.gc.mimicry.core.messaging.Topic;
import com.gc.mimicry.core.messaging.TopicSession;
import com.google.common.base.Preconditions;

public class KeepAliveDriver extends BaseResourceManager implements MessageReceiver, Closeable
{
    public KeepAliveDriver(MessagingSystem messaging, UUID nodeId)
    {
        Preconditions.checkNotNull(messaging);
        Preconditions.checkNotNull(nodeId);

        this.nodeId = nodeId;

        Topic topic = messaging.lookupTopic(SESSION_LOCAL_CHANNEL_MAME);
        TopicSession session = messaging.createTopicSession(topic);
        attachResource(session);
        publisher = session.createPublisher();
        attachResource(publisher);
        subscriber = session.createSubscriber();
        attachResource(subscriber);
        subscriber.setMessageReceiver(this);
    }

    @Override
    public void messageReceived(Topic topic, Message msg)
    {
        if (msg instanceof KeepAliveMessage)
        {
            KeepAliveMessage ka = (KeepAliveMessage) msg;
            if (ka.isRequest())
            {
                publisher.send(new KeepAliveMessage(nodeId));
            }
        }
    }

    private final UUID nodeId;
    private final Publisher publisher;
    private final Subscriber subscriber;
    private static final String SESSION_LOCAL_CHANNEL_MAME = "keepAlive";
}
