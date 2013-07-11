package com.gc.mimicry.core.timing;

/**
 * Simple clock implementation that uses the System clock of the JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class SystemClock implements Clock
{
    @Override
    public long currentMillis()
    {
        return System.currentTimeMillis();
    }

    @Override
    public void sleepUntil(long untilInMillis) throws InterruptedException
    {
        long waitTime = untilInMillis - System.currentTimeMillis();
        if (waitTime > 0)
        {
            Thread.sleep(waitTime);
        }
    }

    @Override
    public void waitOn(Object target) throws InterruptedException
    {
        target.wait();
    }

    @Override
    public void waitOnUntil(Object target, long untilInMillis) throws InterruptedException
    {
        long waitTime = untilInMillis - System.currentTimeMillis();
        if (waitTime > 0)
        {
            target.wait(waitTime);
        }
    }

    @Override
    public void notifyOnTarget(Object target)
    {
        target.notify();
    }

    @Override
    public void notifyAllOnTarget(Object target)
    {
        target.notifyAll();
    }
}
