package com.gc.mimicry.core.timing.net;

import java.io.Closeable;

import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.DiscreteClock;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.events.clock.ClockAdvanceEvent;
import com.gc.mimicry.shared.events.clock.ClockEvent;
import com.gc.mimicry.shared.events.clock.ClockStartEvent;
import com.gc.mimicry.shared.events.clock.ClockStopEvent;
import com.google.common.base.Preconditions;

/**
 * Attach an instance of this class to a clock to expose it to clock events that are emitted for instance by an
 * {@link ClockController}.
 * 
 * @author Marc-Christian Schulze
 * @see ClockStartEvent
 * @see ClockStopEvent
 * @see ClockAdvanceEvent
 */
public class ClockDriver implements Closeable, EventListener
{
    private final EventBroker broker;
    private final Clock clock;

    /**
     * Registers a listener on the given event broker and adjusts the clock if necessary.
     * 
     * @param broker
     *            The broker to listen to
     * @param clock
     *            The clock to adjust
     */
    public ClockDriver(EventBroker broker, Clock clock)
    {
        Preconditions.checkNotNull(broker);
        Preconditions.checkNotNull(clock);

        this.broker = broker;
        this.clock = clock;

        broker.addEventListener(this);
    }

    /**
     * Detaches this instance from the event broker so that it can be picked up by the garbage collector.
     */
    @Override
    public void close()
    {
        broker.removeEventListener(this);
    }

    @Override
    public void handleEvent(Event evt)
    {
        if (evt instanceof ClockEvent)
        {
            handleClockEvent((ClockEvent) evt);
        }
    }

    private void handleClockEvent(ClockEvent evt)
    {
        if (evt instanceof ClockStartEvent)
        {
            tryToStartClock(((ClockStartEvent) evt).getMultiplier());
        }
        else if (evt instanceof ClockStopEvent)
        {
            tryToStopClock();
        }
        else if (evt instanceof ClockAdvanceEvent)
        {
            tryToAdvanceClock(((ClockAdvanceEvent) evt).getDeltaMillis());
        }
    }

    private void tryToStartClock(double multiplier)
    {
        if (clock instanceof RealtimeClock)
        {
            ((RealtimeClock) clock).start(multiplier);
        }
    }

    private void tryToStopClock()
    {
        if (clock instanceof RealtimeClock)
        {
            ((RealtimeClock) clock).stop();
        }
    }

    private void tryToAdvanceClock(long deltaMillis)
    {
        if (clock instanceof DiscreteClock)
        {
            ((DiscreteClock) clock).advance(deltaMillis);
        }
    }
}
