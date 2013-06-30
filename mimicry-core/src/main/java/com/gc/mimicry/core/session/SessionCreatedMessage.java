package com.gc.mimicry.core.session;

import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.net.NodeInfo;

public class SessionCreatedMessage extends Message
{
    private static final long serialVersionUID = -3744393592368465852L;
    private final UUID sessionId;
    private final Set<NodeInfo> participants;

    public SessionCreatedMessage(UUID sessionId, Set<NodeInfo> participants)
    {
        this.sessionId = sessionId;
        this.participants = participants;
    }

    public UUID getSessionId()
    {
        return sessionId;
    }

    public Set<NodeInfo> getParticipants()
    {
        return participants;
    }
}
