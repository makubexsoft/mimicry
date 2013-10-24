package com.gc.mimicry.engine.event;

import java.util.UUID;

import com.gc.mimicry.util.VectorClock;

public abstract class ApplicationEventBase implements ApplicationEvent
{
    private static final long serialVersionUID = -2493518151184299316L;
    private final VectorClock<UUID> id;
    private final UUID controlFlow;
    private final UUID applicationId;

    protected ApplicationEventBase(VectorClock<UUID> id, UUID applicationId)
    {
        this.id = id;
        this.applicationId = applicationId;
        controlFlow = null;
    }

    protected ApplicationEventBase(VectorClock<UUID> id, UUID applicationId, UUID controlFlowId)
    {
        this.id = id;
        this.controlFlow = controlFlowId;
        this.applicationId = applicationId;
    }

    @Override
    public VectorClock<UUID> getClock()
    {
        return id;
    }

    @Override
    public UUID getControlFlow()
    {
        return controlFlow;
    }

    @Override
    public UUID getApplication()
    {
        return applicationId;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("application=");
        builder.append(applicationId);
        builder.append(", controlFlow=");
        builder.append(controlFlow);
        return builder.toString();
    }
}
