package com.gc.mimicry.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFuture<T extends Future<T>>
{

    private final Object lock = new Object();
    private boolean cancelled;
    private boolean success;
    private Throwable cause;

    public boolean isDone()
    {
        return isSuccess() || isCancelled() || cause != null;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

    public Throwable getCause()
    {
        return cause;
    }

    protected void doSynchronized(Runnable job)
    {
        synchronized (lock)
        {
            job.run();
        }
    }

    protected <V> V doSynchronized(Callable<V> job) throws Exception
    {
        synchronized (lock)
        {
            return job.call();
        }
    }

    public boolean setSuccess()
    {
        synchronized (lock)
        {
            if (isDone())
            {
                return false;
            }
            success = true;
            notifyListener();
            lock.notifyAll();
        }
        return true;
    }

    public boolean setFailure(Throwable cause)
    {
        synchronized (lock)
        {
            if (isDone())
            {
                return false;
            }
            this.cause = cause;
            notifyListener();
            lock.notifyAll();
        }
        return true;
    }

    protected abstract boolean performCancellation();

    public boolean cancel()
    {
        synchronized (lock)
        {
            if (!isDone() && performCancellation())
            {
                cancelled = true;
                notifyListener();
                return true;
            }
            return false;
        }
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException
    {
        return await(unit.toMillis(timeout));
    }

    public boolean await(long timeoutInMillis) throws InterruptedException
    {
        synchronized (lock)
        {
            if (!isDone())
            {
                lock.wait(timeoutInMillis);
            }
        }
        return isDone();
    }

    public boolean awaitUninterruptibly(long timeout, TimeUnit unit)
    {
        return awaitUninterruptibly(unit.toMillis(timeout));
    }

    public boolean awaitUninterruptibly(long timeoutInMillis)
    {
        synchronized (lock)
        {
            long start;
            while (timeoutInMillis > 0 && !isDone())
            {
                start = System.currentTimeMillis();
                try
                {
                    lock.wait(timeoutInMillis);
                }
                catch (InterruptedException e)
                {
                }
                timeoutInMillis -= (System.currentTimeMillis() - start);
            }
        }
        return isDone();
    }

    protected abstract void notifyListener();
}
