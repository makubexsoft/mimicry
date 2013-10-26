package org.mimicry.engine.timing;

import java.io.Closeable;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.mimicry.util.concurrent.DefaultValueFuture;
import org.mimicry.util.concurrent.ValueFuture;

import com.google.common.base.Preconditions;

/**
 * This scheduler executes all schedules job in a single daemon thread that is created per instance of this class.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockBasedScheduler implements Scheduler, Closeable
{
    private volatile boolean shouldRun = true;
    private final Thread thread;
    private final Timeline clock;
    private final PriorityQueue<ScheduledJob> jobs;

    public ClockBasedScheduler(Timeline clock)
    {
        Preconditions.checkNotNull(clock);

        this.clock = clock;

        jobs = new PriorityQueue<ScheduledJob>(10, new ScheduledJobComparator());
        thread = new Thread(new JobExecutor());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void schedule(Runnable job, long delay, TimeUnit unit)
    {
        if (delay < 0)
        {
            throw new IllegalArgumentException("Delay must be non-negative.");
        }
        synchronized (jobs)
        {
            jobs.add(new ScheduledJob(clock.currentMillis() + unit.toMillis(delay), job));
            clock.notifyAllOnTarget(jobs);
        }
    }

    @Override
    public <T> ValueFuture<T> schedule(Callable<T> job, long delay, TimeUnit unit)
    {
        if (delay < 0)
        {
            throw new IllegalArgumentException("Delay must be non-negative.");
        }
        synchronized (jobs)
        {
            ScheduledCallableJob<T> e = new ScheduledCallableJob<T>(clock.currentMillis() + unit.toMillis(delay), job);
            jobs.add(e);
            clock.notifyAllOnTarget(jobs);
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

        long getTimeInMillis()
        {
            return timeInMillis;
        }
    }

    private static class ScheduledJobComparator implements Comparator<ScheduledJob>
    {

        @Override
        public int compare(ScheduledJob o1, ScheduledJob o2)
        {
            return new Long(o1.getTimeInMillis()).compareTo(o2.getTimeInMillis());
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
                    if (!jobs.isEmpty())
                    {
                        ScheduledJob nextJob = jobs.peek();
                        if (nextJob.timeInMillis <= clock.currentMillis())
                        {
                            jobs.remove();
                            return nextJob.runnable;
                        }
                    }

                    waitForNextJob();
                }
                return new NoOperation();
            }
        }

        private void waitForNextJob()
        {
            synchronized (jobs)
            {
                try
                {
                    long waitUntilInMillis = Long.MAX_VALUE;
                    if (!jobs.isEmpty())
                    {
                        waitUntilInMillis = jobs.peek().getTimeInMillis();
                    }
                    clock.waitOnUntil(jobs, waitUntilInMillis);
                }
                catch (InterruptedException e)
                {
                    // suppress
                }
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
