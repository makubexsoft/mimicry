package com.gc.mimicry.engine.nodes.events;

import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.event.Event;

public interface CreateNodeEvent extends Event
{
    public NodeParameters getNodeConfig();

    public void setNodeConfig(NodeParameters value);
}
