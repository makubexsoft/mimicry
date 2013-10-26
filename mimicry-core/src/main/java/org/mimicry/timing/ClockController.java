package org.mimicry.timing;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.streams.TimelineStream;

import com.google.common.base.Preconditions;

/**
 * This class allows remotely controlling the clock of a simulation.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockController
{
    private final Stream stream;

    public ClockController(CEPEngine eventEngine)
    {
        Preconditions.checkNotNull(eventEngine);
        stream = TimelineStream.get(eventEngine);
    }

    public void start(double multiplier)
    {
        stream.send(TimelineStream.COMMAND_START, multiplier, 0);
    }

    public void stop()
    {
        stream.send(TimelineStream.COMMAND_STOP, 0, 0);
    }

    public void advance(long deltaMillis)
    {
        stream.send(TimelineStream.COMMAND_ADVANCE, 0, deltaMillis);
    }
}
