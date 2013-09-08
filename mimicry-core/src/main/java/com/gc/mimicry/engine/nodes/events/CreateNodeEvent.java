package com.gc.mimicry.engine.nodes.events;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.nodes.NodeConfiguration;

public interface CreateNodeEvent extends Event
{
    public NodeConfiguration getNodeConfig();

    public void setNodeConfig(NodeConfiguration value);
}
