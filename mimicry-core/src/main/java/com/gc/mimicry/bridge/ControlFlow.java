package com.gc.mimicry.bridge;

import java.util.UUID;

import com.gc.mimicry.engine.event.ApplicationEvent;
import com.gc.mimicry.util.concurrent.DefaultValueFuture;

public class ControlFlow
{
    private final DefaultValueFuture<ApplicationEvent> future;
    private final UUID id;

    public ControlFlow()
    {
        future = new DefaultValueFuture<ApplicationEvent>();
        id = UUID.randomUUID();
    }

    public ApplicationEvent awaitTermination()
    {
        future.awaitUninterruptibly(Long.MAX_VALUE);
        return getTerminationCause();
    }

    public void awaitTermination(long timeoutMillis)
    {
        future.awaitUninterruptibly(timeoutMillis);
    }

    /**
     * Terminates this control flow so that the blocked thread continues running.
     * 
     * @param event
     */
    public void terminate(ApplicationEvent event)
    {
        future.setValue(event);
    }

    public ApplicationEvent getTerminationCause()
    {
        return future.getValue();
    }

    public UUID getId()
    {
        return id;
    }
}
