package com.gc.mimicry.ext.timing;

import com.gc.mimicry.engine.EventBroker;
import com.gc.mimicry.ext.timing.events.ClockAdvanceEvent;
import com.gc.mimicry.ext.timing.events.ClockStartEvent;
import com.gc.mimicry.ext.timing.events.ClockStopEvent;

/**
 * This class allows remotely controling the clock of a simulation.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockController
{
    private final EventBroker broker;

    public ClockController(EventBroker broker)
    {
        this.broker = broker;
    }

    public void start(double multiplier)
    {
        broker.fireEvent(new ClockStartEvent(multiplier));
    }

    public void stop()
    {
        broker.fireEvent(new ClockStopEvent());
    }

    public void advance(long deltaMillis)
    {
        broker.fireEvent(new ClockAdvanceEvent(deltaMillis));
    }
}
