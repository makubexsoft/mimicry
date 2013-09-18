package com.gc.mimicry.ext.timing;

import java.io.Closeable;

import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.engine.timing.DiscreteClock;
import com.gc.mimicry.engine.timing.RealtimeClock;
import com.gc.mimicry.ext.timing.events.ClockAdvanceEvent;
import com.gc.mimicry.ext.timing.events.ClockEvent;
import com.gc.mimicry.ext.timing.events.ClockStartEvent;
import com.gc.mimicry.ext.timing.events.ClockStopEvent;
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
    private final EventEngine broker;
    private final Timeline clock;

    /**
     * Registers a listener on the given event broker and adjusts the clock if necessary.
     * 
     * @param broker
     *            The broker to listen to
     * @param clock
     *            The clock to adjust
     */
    public ClockDriver(EventEngine broker, Timeline clock)
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
