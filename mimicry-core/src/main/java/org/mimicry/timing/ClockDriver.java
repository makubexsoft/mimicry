package org.mimicry.timing;

import java.io.Closeable;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Event;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamListener;
import org.mimicry.streams.TimelineStream;

import com.google.common.base.Preconditions;

/**
 * Attach an instance of this class to a clock to expose it to clock events that are emitted for instance by an
 * {@link ClockController}.
 * 
 * @author Marc-Christian Schulze
 */
public class ClockDriver implements Closeable, StreamListener
{
    private final Timeline timeline;
    private final Stream stream;

    /**
     * Registers a listener on the given event broker and adjusts the clock if necessary.
     * 
     * @param eventEngine
     *            The broker to listen to
     * @param timeline
     *            The clock to adjust
     */
    public ClockDriver(CEPEngine eventEngine, Timeline timeline)
    {
        Preconditions.checkNotNull(eventEngine);
        Preconditions.checkNotNull(timeline);

        this.timeline = timeline;

        stream = TimelineStream.get(eventEngine);
        stream.addStreamListener(this);
    }

    /**
     * Detaches this instance from the event broker so that it can be picked up by the garbage collector.
     */
    @Override
    public void close()
    {
        stream.removeStreamListener(this);
    }

    @Override
    public void receive(Event[] events)
    {
        for (Event event : events)
        {
            handleEvent(event);
        }
    }

    private void handleEvent(Event evt)
    {
        String command = TimelineStream.getCommand(evt);
        if (command == TimelineStream.COMMAND_START)
        {
            tryToStartClock(TimelineStream.getMultiplier(evt));
        }
        else if (command == TimelineStream.COMMAND_STOP)
        {
            tryToStopClock();
        }
        else if (command == TimelineStream.COMMAND_ADVANCE)
        {
            tryToAdvanceClock(TimelineStream.getDeltaT(evt));
        }
    }

    private void tryToStartClock(double multiplier)
    {
        if (timeline instanceof RealtimeClock)
        {
            ((RealtimeClock) timeline).start(multiplier);
        }
    }

    private void tryToStopClock()
    {
        if (timeline instanceof RealtimeClock)
        {
            ((RealtimeClock) timeline).stop();
        }
    }

    private void tryToAdvanceClock(long deltaMillis)
    {
        if (timeline instanceof DiscreteClock)
        {
            ((DiscreteClock) timeline).advance(deltaMillis);
        }
    }
}
