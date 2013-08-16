package com.gc.mimicry.cluster.messaging.p2p;

import com.gc.mimicry.cluster.messaging.Message;

public class MessageEnvelope extends Message
{

    private static final long serialVersionUID = 6869966859004692299L;
    private String topicName;
    private Message content;

    public MessageEnvelope(Message content, String topicName)
    {
        super(content.getType());
        this.content = content;
        this.topicName = topicName;
    }

    public Message getContent()
    {
        return content;
    }

    public String getTopicName()
    {
        return topicName;
    }
}