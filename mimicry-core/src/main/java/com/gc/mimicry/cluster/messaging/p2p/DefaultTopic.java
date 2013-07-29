package com.gc.mimicry.cluster.messaging.p2p;

import com.gc.mimicry.cluster.messaging.Topic;

public class DefaultTopic implements Topic
{

    private String name;

    DefaultTopic(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
