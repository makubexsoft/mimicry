package com.gc.mimicry.engine.event;

import java.util.UUID;

import com.gc.mimicry.util.VectorClock;

public abstract class EventBase implements Event
{
    private static final long serialVersionUID = -2493518151184299316L;
    private final VectorClock<UUID> clock;
    private UUID controlFlow;
    private UUID sourceId;
    private UUID destId;

    protected EventBase(VectorClock<UUID> clock, UUID controlFlow, UUID sourceId, UUID destId)
    {
        this.clock = clock;
        this.controlFlow = controlFlow;
        this.sourceId = sourceId;
        this.destId = destId;
    }

    protected EventBase(VectorClock<UUID> clock, UUID controlFlow, UUID sourceId)
    {
        this.clock = clock;
        this.controlFlow = controlFlow;
        this.sourceId = sourceId;
    }

    protected EventBase(VectorClock<UUID> clock, UUID destId)
    {
        this.clock = clock;
        this.destId = destId;
    }

    protected EventBase(VectorClock<UUID> clock)
    {
        this.clock = clock;
    }

    @Override
    public VectorClock<UUID> getClock()
    {
        return clock;
    }

    @Override
    public UUID getAssociatedControlFlow()
    {
        return controlFlow;
    }

    @Override
    public UUID getSourceApplication()
    {
        return sourceId;
    }

    @Override
    public UUID getTargetApplication()
    {
        return destId;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("controlFlow=");
        builder.append(controlFlow);
        builder.append(", sourceId=");
        builder.append(sourceId);
        builder.append(", destId=");
        builder.append(destId);
        return builder.toString();
    }
}
