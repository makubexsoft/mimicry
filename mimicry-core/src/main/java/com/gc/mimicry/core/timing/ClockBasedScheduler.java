package com.gc.mimicry.core.timing;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.util.concurrent.DefaultValueFuture;
import com.gc.mimicry.util.concurrent.ValueFuture;
import com.google.common.base.Preconditions;

/**
 * This scheduler executes all schedules job in a single daemon thread that is created per instance of this class.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockBasedScheduler implements Scheduler, Closeable
{

    private static final int JOB_CHECKING_DELAY_MILLIS = 100;
    private volatile boolean shouldRun = true;
    private final Thread thread;
    private final Clock clock;
    private final List<ScheduledJob> jobs;

    public ClockBasedScheduler(Clock clock)
    {
        Preconditions.checkNotNull(clock);
        this.clock = clock;
        jobs = new CopyOnWriteArrayList<ScheduledJob>();
        thread = new Thread(new JobExecutor());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void schedule(Runnable job, long delay, TimeUnit unit)
    {
        synchronized (jobs)
        {
            jobs.add(new ScheduledJob(clock.currentMillis() + unit.toMillis(delay), job));
            jobs.notify();
        }
    }

    @Override
    public <T> ValueFuture<T> schedule(Callable<T> job, long delay, TimeUnit unit)
    {
        synchronized (jobs)
        {
            ScheduledCallableJob<T> e = new ScheduledCallableJob<T>(clock.currentMillis() + unit.toMillis(delay), job);
            jobs.add(e);
            jobs.notify();
            return e.getFuture();
        }
    }

    @Override
    public void close()
    {
        shouldRun = false;
    }

    private static class ScheduledJob
    {
        public long timeInMillis;
        public Runnable runnable;

        public ScheduledJob(long timeInMillis, Runnable job)
        {
            this.timeInMillis = timeInMillis;
            this.runnable = job;
        }

        public ScheduledJob(long timeInMillis)
        {
            this.timeInMillis = timeInMillis;
        }

        protected void setJob(Runnable job)
        {
            runnable = job;
        }
    }

    private static class ScheduledCallableJob<T> extends ScheduledJob
    {
        private final DefaultValueFuture<T> future = new DefaultValueFuture<T>();

        public ScheduledCallableJob(long timeInMillis, final Callable<T> job)
        {
            super(timeInMillis);
            setJob(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        future.setValue(job.call());
                    }
                    catch (Throwable th)
                    {
                        future.setFailure(th);
                    }
                }
            });
        }

        public ValueFuture<T> getFuture()
        {
            return future;
        }
    }

    private class JobExecutor implements Runnable
    {

        @Override
        public void run()
        {
            while (shouldRun)
            {
                Runnable job = takeNextJob();
                job.run();
            }
        }

        private Runnable takeNextJob()
        {
            synchronized (jobs)
            {
                while (shouldRun)
                {
                    ScheduledJob nextJob = null;
                    for (ScheduledJob job : jobs)
                    {
                        if (job.timeInMillis <= clock.currentMillis())
                        {
                            nextJob = job;
                            break;
                        }
                    }
                    if (nextJob != null)
                    {
                        jobs.remove(nextJob);
                        return nextJob.runnable;
                    }
                    try
                    {
                        jobs.wait(JOB_CHECKING_DELAY_MILLIS);
                    }
                    catch (InterruptedException e)
                    {
                        // suppress
                    }
                }
                return new NoOperation();
            }
        }
    }

    private static class NoOperation implements Runnable
    {

        @Override
        public void run()
        {
        }
    }

}
