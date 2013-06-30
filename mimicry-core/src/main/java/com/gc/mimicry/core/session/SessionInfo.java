package com.gc.mimicry.core.session;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.net.NodeInfo;

public class SessionInfo implements Serializable
{
    private static final long serialVersionUID = -8536939513670139738L;
    private final UUID sessionId;
    private final Set<NodeInfo> participants;

    public SessionInfo(UUID sessionId, Set<NodeInfo> participants)
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        SessionInfo other = (SessionInfo) obj;
        if (sessionId == null)
        {
            if (other.sessionId != null)
            {
                return false;
            }
        }
        else if (!sessionId.equals(other.sessionId))
        {
            return false;
        }
        return true;
    }
}
