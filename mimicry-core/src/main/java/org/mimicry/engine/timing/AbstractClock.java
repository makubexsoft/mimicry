package org.mimicry.engine.timing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.mimicry.bridge.threading.IManagedThread;
import org.mimicry.bridge.threading.ThreadShutdownListener;


/**
 * Abstract base class that implements all threading related functionality.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public abstract class AbstractClock implements Timeline, ThreadShutdownListener
{

    private static long STATE_CHECKING_DELAY_IN_MILLIS = 10;
    private final WeakHashMap<IManagedThread, Object> blockedThreads;
    private final Set<Object> notifications;

    public AbstractClock()
    {
        blockedThreads = new WeakHashMap<IManagedThread, Object>();
        notifications = Collections.synchronizedSet(new HashSet<Object>());
    }

    @Override
    public void sleepFor(long timeInMillis) throws InterruptedException
    {
        long until = currentMillis() + timeInMillis;
        sleepUntil(until);
    }

    @Override
    public void sleepUntil(long untilInMillis) throws InterruptedException
    {
        Thread thread = Thread.currentThread();
        if (thread instanceof IManagedThread)
        {
            IManagedThread managedThread = (IManagedThread) thread;
            synchronized (blockedThreads)
            {
                managedThread.addThreadShutdownListener(this);
            }
        }

        while (currentMillis() < untilInMillis)
        {
            try
            {
                Thread.sleep(STATE_CHECKING_DELAY_IN_MILLIS);
            }
            finally
            {
                if (thread instanceof IManagedThread)
                {
                    if (((IManagedThread) thread).isShuttingDown())
                    {
                        throw new ThreadDeath();
                    }
                }
            }

        }
    }

    @Override
    public void waitOn(Object target) throws InterruptedException
    {
        Thread thread = Thread.currentThread();
        if (thread instanceof IManagedThread)
        {
            IManagedThread managedThread = (IManagedThread) thread;
            synchronized (blockedThreads)
            {
                managedThread.addThreadShutdownListener(this);
                blockedThreads.put(managedThread, target);
            }
        }
        try
        {
            target.wait();
        }
        finally
        {
            if (thread instanceof IManagedThread)
            {
                IManagedThread managedThread = (IManagedThread) thread;
                if (managedThread.isShuttingDown())
                {
                    throw new ThreadDeath();
                }
            }
        }
    }

    @Override
    public void waitOnFor(Object target, long timeoutInMillis) throws InterruptedException
    {
        long timeUntil = currentMillis() + timeoutInMillis;
        waitOnUntil(target, timeUntil);
    }

    @Override
    public void waitOnUntil(Object target, long timeUntilInMillis) throws InterruptedException
    {
        Thread thread = Thread.currentThread();
        if (thread instanceof IManagedThread)
        {
            IManagedThread managedThread = (IManagedThread) thread;
            synchronized (blockedThreads)
            {
                managedThread.addThreadShutdownListener(this);
                blockedThreads.put(managedThread, target);
            }
        }

        try
        {
            while (currentMillis() < timeUntilInMillis)
            {
                try
                {
                    target.wait(STATE_CHECKING_DELAY_IN_MILLIS);
                    if (notifications.contains(target))
                    {
                        notifications.remove(target);
                        return;
                    }
                }
                finally
                {
                    if (thread instanceof IManagedThread)
                    {
                        if (((IManagedThread) thread).isShuttingDown())
                        {
                            throw new ThreadDeath();
                        }
                    }
                }
            }
        }
        finally
        {
            if (thread instanceof IManagedThread)
            {
                synchronized (blockedThreads)
                {
                    blockedThreads.remove(thread);
                }
            }
        }
    }

    @Override
    public void threadShouldTerminate(IManagedThread thread)
    {
        synchronized (blockedThreads)
        {
            thread.removeThreadShutdownListener(this);
            Object object = blockedThreads.remove(thread);
            if (object != null)
            {
                synchronized (object)
                {
                    object.notifyAll();
                }
            }
        }
    }

    @Override
    public void notifyOnTarget(Object target)
    {
        notifications.add(target);
        Thread thread = Thread.currentThread();
        if (thread instanceof IManagedThread)
        {
            IManagedThread managedThread = (IManagedThread) thread;
            if (managedThread.isShuttingDown())
            {
                target.notifyAll();
            }
            else
            {
                target.notify();
            }
        }
        else
        {
            target.notify();
        }
    }

    @Override
    public void notifyAllOnTarget(Object target)
    {
        notifications.add(target);
        target.notifyAll();
    }
}
