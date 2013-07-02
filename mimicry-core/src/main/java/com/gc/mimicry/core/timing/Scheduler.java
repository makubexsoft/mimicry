package com.gc.mimicry.core.timing;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.util.concurrent.ValueFuture;

public interface Scheduler
{
    public void schedule(Runnable job, long delay, TimeUnit unit);

    public <T> ValueFuture<T> schedule(Callable<T> job, long delay, TimeUnit unit);
}
