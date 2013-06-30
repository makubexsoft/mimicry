package com.gc.mimicry.core.cflow;

import java.util.UUID;

import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;

public class ControlFlow
{
    private Future<?> future;
    private UUID id;

    public ControlFlow()
    {
        future = new DefaultFuture();
        id = UUID.randomUUID();
    }

    public Future<?> getFuture()
    {
        return future;
    }

    public UUID getId()
    {
        return id;
    }
}
