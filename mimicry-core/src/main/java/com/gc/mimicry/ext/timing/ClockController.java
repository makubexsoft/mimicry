package com.gc.mimicry.ext.timing;

import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.ext.timing.events.ClockAdvanceEvent;
import com.gc.mimicry.ext.timing.events.ClockStartEvent;
import com.gc.mimicry.ext.timing.events.ClockStopEvent;
import com.google.common.base.Preconditions;

/**
 * This class allows remotely controling the clock of a simulation.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockController
{
    private final EventEngine broker;
    private final EventFactory eventFactory;

    public ClockController(EventEngine broker, EventFactory eventFactory)
    {
        Preconditions.checkNotNull(broker);
        Preconditions.checkNotNull(eventFactory);

        this.broker = broker;
        this.eventFactory = eventFactory;
    }

    public void start(double multiplier)
    {
        ClockStartEvent event = eventFactory.createEvent(ClockStartEvent.class);
        event.setMultiplier(multiplier);
        broker.fireEvent(event);
    }

    public void stop()
    {
        ClockStopEvent event = eventFactory.createEvent(ClockStopEvent.class);
        broker.fireEvent(event);
    }

    public void advance(long deltaMillis)
    {
        ClockAdvanceEvent event = eventFactory.createEvent(ClockAdvanceEvent.class);
        event.setDeltaMillis(deltaMillis);
        broker.fireEvent(event);
    }
}
