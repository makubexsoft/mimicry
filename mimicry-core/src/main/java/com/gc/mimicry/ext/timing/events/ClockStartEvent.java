package com.gc.mimicry.ext.timing.events;

import com.gc.mimicry.engine.event.Event;

/**
 * Starts the realtime clock if installed. This event is evaluated by all ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ClockStartEvent extends Event, ClockEvent
{
    public double getMultiplier();

    public void setMultiplier(double value);
}
