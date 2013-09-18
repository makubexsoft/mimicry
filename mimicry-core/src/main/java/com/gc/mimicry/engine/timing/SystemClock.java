package com.gc.mimicry.engine.timing;

/**
 * Simple clock implementation that uses the System clock of the JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class SystemClock implements Timeline
{
    @Override
    public long currentMillis()
    {
        return System.currentTimeMillis();
    }

    @Override
    public void sleepFor(long waitTime) throws InterruptedException
    {
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
    public void waitOnFor(Object target, long untilInMillis) throws InterruptedException
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

    @Override
    public void sleepUntil(long timeUntilInMillis) throws InterruptedException
    {
        long toWait = timeUntilInMillis - currentMillis();
        if (toWait > 0)
        {
            sleepFor(toWait);
        }
    }

    @Override
    public void waitOnUntil(Object target, long timeUntilInMillis) throws InterruptedException
    {
        long toWait = timeUntilInMillis - currentMillis();
        if (toWait > 0)
        {
            waitOnFor(target, toWait);
        }
    }
}
