package com.gc.mimicry.engine.nodes.events;

import com.gc.mimicry.engine.BaseEvent;
import com.gc.mimicry.engine.nodes.NodeRef;

public class NodeCreatedEvent extends BaseEvent
{
    private final NodeRef nodeRef;

    public NodeCreatedEvent(NodeRef nodeRef)
    {
        super();
        this.nodeRef = nodeRef;
    }

    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
}
