package com.gc.mimicry.engine.nodes.events;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.nodes.NodeRef;

public interface NodeCreatedEvent extends Event
{
    public NodeRef getNodeRef();

    public void setNodeRef(NodeRef value);
}
