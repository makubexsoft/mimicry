package com.gc.mimicry.bridge.cflow;

import java.util.UUID;

import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.util.concurrent.DefaultValueFuture;

public class ControlFlow
{
    private final DefaultValueFuture<Event> future;
    private final UUID id;

    public ControlFlow()
    {
        future = new DefaultValueFuture<Event>();
        id = UUID.randomUUID();
    }

    public void awaitTermination()
    {
        future.awaitUninterruptibly(Long.MAX_VALUE);
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
    public void terminate(Event event)
    {
        future.setValue(event);
    }

    public Event getTerminationCause()
    {
        return future.getValue();
    }

    public UUID getId()
    {
        return id;
    }
}
