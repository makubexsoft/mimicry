package com.gc.mimicry.shared.events;

import java.util.UUID;


public class BaseEvent implements Event
{
    public BaseEvent(UUID appId, UUID controlFlowId)
    {
        this.appId = appId;
        this.controlFlowId = controlFlowId;
    }

    public BaseEvent(UUID appId)
    {
        this.appId = appId;
    }

    @Override
    public UUID getControlFlowId()
    {
        return controlFlowId;
    }

    @Override
    public UUID getDestinationAppId()
    {
        return appId;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseEvent [controlFlowId=");
        builder.append(controlFlowId);
        builder.append(", appId=");
        builder.append(appId);
        builder.append("]");
        return builder.toString();
    }

    private static final long serialVersionUID = 1656650145236686849L;
    private UUID controlFlowId;
    private final UUID appId;
}
