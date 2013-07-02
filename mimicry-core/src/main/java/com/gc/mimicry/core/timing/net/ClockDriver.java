package com.gc.mimicry.core.timing.net;

import java.io.Closeable;

import com.gc.mimicry.core.event.Node;
import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.messaging.MessageReceiver;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.Topic;
import com.google.common.base.Preconditions;

public class ClockDriver implements Closeable, MessageReceiver
{

    private final MessagingSystem messaging;
    private final Node node;

    public ClockDriver(MessagingSystem messaging, Node node)
    {
        Preconditions.checkNotNull(messaging);
        Preconditions.checkNotNull(node);

        this.messaging = messaging;
        this.node = node;
    }

    @Override
    public void close()
    {

    }

    @Override
    public void messageReceived(Topic topic, Message msg)
    {
        // TODO Auto-generated method stub
        // set clock of apps running on node as well as for each event handler
    }
}
