package com.gc.mimicry.ext.timing.events;

import com.gc.mimicry.engine.event.Event;

/**
 * This event type advances the discrete clock by a certain amount of time iff installed. This event is evaluated by all
 * ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ClockAdvanceEvent extends Event, ClockEvent
{
    /**
     * The delta T (in milliseconds) by which the clock should be advanced.
     * 
     * @return
     */
    public long getDeltaMillis();

    public void setDeltaMillis(long value);
}
