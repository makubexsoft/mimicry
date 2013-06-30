package com.gc.mimicry.bridge.cflow;

import java.util.UUID;

import com.gc.mimicry.core.event.Event;
import com.gc.mimicry.util.concurrent.DefaultValueFuture;
import com.gc.mimicry.util.concurrent.ValueFuture;

public class ControlFlow
{
    private final ValueFuture<Event> future;
    private final UUID id;
    private Event cause;

    public ControlFlow()
    {
        future = new DefaultValueFuture<Event>();
        id = UUID.randomUUID();
    }

    public void awaitTermination()
    {

    }

    public void awaitTermination(long timeoutMillis)
    {

    }

    /**
     * Terminates this control flow so that the blocked thread continues running.
     * 
     * @param event
     */
    public void terminate(Event event)
    {
        cause = event;
    }

    public Event getTerminationCause()
    {
        return cause;
    }

    public UUID getId()
    {
        return id;
    }
}
