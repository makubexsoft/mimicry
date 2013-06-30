package com.gc.mimicry.core.session.keepalive;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;

public class KeepAliveMessage extends Message
{
    public KeepAliveMessage()
    {
        nodeId = null;
        request = true;
    }

    public KeepAliveMessage(UUID nodeId)
    {
        this.nodeId = nodeId;
        this.request = false;
    }

    public boolean isRequest()
    {
        return request;
    }

    public boolean isResponse()
    {
        return !isRequest();
    }

    public UUID getNodeId()
    {
        return nodeId;
    }

    private final boolean request;
    private final UUID nodeId;
    private static final long serialVersionUID = -6992169211400670661L;
}
