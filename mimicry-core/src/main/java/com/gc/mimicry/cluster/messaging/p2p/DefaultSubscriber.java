package com.gc.mimicry.cluster.messaging.p2p;

import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.cluster.messaging.MessageReceiver;
import com.gc.mimicry.cluster.messaging.Subscriber;
import com.gc.mimicry.cluster.messaging.Topic;

public class DefaultSubscriber implements Subscriber
{

    private DefaultTopicSession session;
    private Topic topic;
    private MessageReceiver receiver;

    DefaultSubscriber(DefaultTopicSession session, Topic topic)
    {
        this.session = session;
        this.topic = topic;
    }

    void deliver(Message msg)
    {
        if (receiver != null)
        {
            receiver.messageReceived(topic, msg);
        }
    }

    public Topic getTopic()
    {
        return topic;
    }

    public void setMessageReceiver(MessageReceiver receiver)
    {
        this.receiver = receiver;
    }

    public void close()
    {
        session.unsubscribe(this);
        session = null;
        receiver = null;
    }
}
