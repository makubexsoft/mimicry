package com.gc.mimicry.engine.timing;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.util.concurrent.ValueFuture;

/**
 * Schedulers allow to run certain jobs asynchronously.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface Scheduler
{
    /**
     * Schedules the given job with the given delay.
     * 
     * @param job
     *            The job to schedule.
     * @param delay
     *            The delay in units of time.
     * @param unit
     *            The units of time.
     */
    public void schedule(Runnable job, long delay, TimeUnit unit);

    /**
     * Schedules the given job and returns a future for observing it's success.
     * 
     * @param job
     *            The job to schedule.
     * @param delay
     *            The delay in units of time.
     * @param unit
     *            The units of time.
     * @return A future to observe the status of the job.
     */
    public <T> ValueFuture<T> schedule(Callable<T> job, long delay, TimeUnit unit);
}
