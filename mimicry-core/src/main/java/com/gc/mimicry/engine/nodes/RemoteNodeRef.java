package com.gc.mimicry.engine.nodes;

import java.util.UUID;


public class RemoteNodeRef implements NodeRef
{
    private final UUID id;

    public RemoteNodeRef(UUID id)
    {
        super();
        this.id = id;
    }

    @Override
    public UUID getNodeId()
    {
        return id;
    }

}
