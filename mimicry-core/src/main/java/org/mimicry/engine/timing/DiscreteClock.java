package org.mimicry.engine.timing;

/**
 * This clock models a discrete time line. To advance the timeline you have to invoke {@link #advance(long)} manually.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class DiscreteClock extends AbstractClock
{

    private volatile long currentMillis;

    /**
     * Creates a new clock setting the current time to the given milliseconds.
     * 
     * @param initialMillis
     */
    public DiscreteClock(long initialMillis)
    {
        currentMillis = initialMillis;
    }

    public long currentMillis()
    {
        return currentMillis;
    }

    /**
     * Advances the timeline by the given milliseconds.
     * 
     * @param deltaTMillis
     */
    public void advance(long deltaTMillis)
    {
        if (deltaTMillis < 0)
        {
            throw new IllegalArgumentException("Can't sample time in the past. delta = " + deltaTMillis);
        }
        currentMillis += deltaTMillis;
    }
}
