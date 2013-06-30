package com.gc.mimicry.core.messaging;

import java.io.Closeable;

public interface TopicSession extends Closeable
{
    public Publisher createPublisher();

    public Subscriber createSubscriber();

    public Topic getTopic();

    public void close();
}
