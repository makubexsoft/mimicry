package com.gc.mimicry.cluster.session;

import java.util.UUID;

import com.gc.mimicry.cluster.messaging.Message;

public class CreateSessionMessage extends Message
{
    private static final long serialVersionUID = 3822321520295993136L;
    private UUID sessionId;

    public CreateSessionMessage(UUID sessionId)
    {
        this.sessionId = sessionId;
    }

    public UUID getSessionId()
    {
        return sessionId;
    }
}
