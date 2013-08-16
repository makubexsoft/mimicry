package com.gc.mimicry.cluster.session.controller;

import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.cluster.NodeInfo;
import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.google.common.base.Preconditions;

public class SessionController
{
    public SessionController(UUID sessionId, Set<NodeInfo> nodes, MessagingSystem messaging)
    {
        Preconditions.checkNotNull(sessionId);
        Preconditions.checkNotNull(nodes);

        this.sessionId = sessionId;
        this.nodes = nodes;
    }

    public UUID getSessionId()
    {
        return sessionId;
    }

    public Set<NodeInfo> getNodes()
    {
        return nodes;
    }

    private final UUID sessionId;
    private final Set<NodeInfo> nodes;
}