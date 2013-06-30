package com.gc.mimicry.core.messaging;

import java.io.Closeable;

public interface MessagingSystem extends Closeable
{

    public Topic lookupTopic(String name);

    public TopicSession createTopicSession(Topic topic);

    public void close();
}
