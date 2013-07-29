package com.gc.mimicry.engine.nodes.events;

import com.gc.mimicry.engine.BaseEvent;
import com.gc.mimicry.engine.nodes.NodeConfiguration;

public class CreateNodeEvent extends BaseEvent
{
    private final NodeConfiguration nodeConfig;

    public CreateNodeEvent(NodeConfiguration nodeConfig)
    {
        super();
        this.nodeConfig = nodeConfig;
    }

    public NodeConfiguration getNodeConfig()
    {
        return nodeConfig;
    }
}
