package com.gc.mimicry.ext.timing.events;

import com.gc.mimicry.engine.event.Event;

/**
 * Stops the real-time clock iff installed. This event is evaluated by all ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ClockStopEvent extends Event, ClockEvent
{

}
