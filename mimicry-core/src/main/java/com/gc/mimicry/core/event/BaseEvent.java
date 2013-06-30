package com.gc.mimicry.core.event;

import java.io.Serializable;
import java.util.UUID;

public class BaseEvent implements Serializable
{
    public BaseEvent(UUID controlFlowId)
    {
        this.controlFlowId = controlFlowId;
    }

    public BaseEvent()
    {
    }

    public UUID getControlFlowId()
    {
        return controlFlowId;
    }

    private static final long serialVersionUID = 1656650145236686849L;
    private UUID controlFlowId;
}
