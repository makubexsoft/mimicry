package com.gc.mimicry.core.session;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.net.NodeInfo;

public class ParticipateInSessionMessage extends Message
{
    private static final long serialVersionUID = 5805939779898272919L;
    private final UUID sessionId;
    private final NodeInfo node;

    public ParticipateInSessionMessage(UUID sessionId, NodeInfo node)
    {
        this.sessionId = sessionId;
        this.node = node;
    }

    public UUID getSessionId()
    {
        return sessionId;
    }

    public NodeInfo getNode()
    {
        return node;
    }
}
