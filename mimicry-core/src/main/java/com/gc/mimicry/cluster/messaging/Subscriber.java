package com.gc.mimicry.cluster.messaging;

import java.io.Closeable;

public interface Subscriber extends Closeable
{

    public Topic getTopic();

    public void setMessageReceiver(MessageReceiver receiver);

    public void close();
}
