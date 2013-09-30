package com.gc.mimicry.engine.nodes.events;

import java.util.UUID;

import com.gc.mimicry.engine.event.Event;

public interface NodeCreatedEvent extends Event
{
    public UUID getNodeId();

    public void setNodeId(UUID id);
}
