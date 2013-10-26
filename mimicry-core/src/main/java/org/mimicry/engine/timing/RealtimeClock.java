package org.mimicry.engine.timing;

/**
 * This clock implementation models a realtime timeline in which the time advances automatically by the given speed
 * multiplier. The clock can be paused and resumed.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class RealtimeClock extends AbstractClock
{

    private final Object lock = new Object();
    private double speedMultiplier;
    private long lastStartMillis;
    private long lastFrozenMillis;

    /**
     * Constructs a new clock with the given millis as current time. The clock is initially stopped and need to be
     * started manually using {@link #start(double)}.
     * 
     * @param initialMillis
     */
    public RealtimeClock(long initialMillis)
    {
        lastFrozenMillis = initialMillis;
    }

    /**
     * Starts the clock with the given speed multiplier.
     * 
     * @param speedMultiplier
     * @throws IllegalStateException
     *             If the clock is already running.
     */
    public void start(double speedMultiplier)
    {
        if (this.speedMultiplier != 0.0)
        {
            throw new IllegalStateException("Clock already running.");
        }
        synchronized (lock)
        {
            this.speedMultiplier = speedMultiplier;
            lastStartMillis = System.currentTimeMillis();
        }
    }

    /**
     * Stops advancing the clock. The clock can be resumed by invoking {@link #start(double)} again.
     * 
     * @return
     */
    public double stop()
    {
        synchronized (lock)
        {
            lastFrozenMillis = currentMillis();
            double tmp = speedMultiplier;
            speedMultiplier = 0.0;
            return tmp;
        }
    }

    @Override
    public long currentMillis()
    {
        synchronized (lock)
        {
            return lastFrozenMillis + (long) ((System.currentTimeMillis() - lastStartMillis) * speedMultiplier);
        }
    }
}
