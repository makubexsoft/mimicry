package com.gc.mimicry.core.timing.net;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;

public class ClockRequestAck extends Message
{
    private static final long serialVersionUID = -1428295848352630716L;
    private final UUID requestId;
    private final UUID nodeId;

    public ClockRequestAck(UUID requestId, UUID nodeId)
    {
        this.requestId = requestId;
        this.nodeId = nodeId;
    }

    public UUID getRequestId()
    {
        return requestId;
    }

    public UUID getNodeId()
    {
        return nodeId;
    }
}
