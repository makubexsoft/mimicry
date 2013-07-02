package com.gc.mimicry.core.runtime;

import java.util.UUID;

import com.gc.mimicry.core.event.Node;
import com.google.common.base.Preconditions;

public class LocalNodeRef implements NodeRef
{
    private final UUID nodeId;

    public LocalNodeRef(Node node)
    {
        Preconditions.checkNotNull(node);

        nodeId = node.getId();
    }

    @Override
    public UUID getNodeId()
    {
        return nodeId;
    }

    @Override
    public String toString()
    {
        return "LocalNodeRef [nodeId=" + nodeId + "]";
    }

}
