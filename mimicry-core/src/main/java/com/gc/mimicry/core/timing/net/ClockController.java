package com.gc.mimicry.core.timing.net;

import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.shared.events.clock.ClockAdvanceEvent;
import com.gc.mimicry.shared.events.clock.ClockStartEvent;
import com.gc.mimicry.shared.events.clock.ClockStopEvent;

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
