package org.mimicry.bridge.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mimicry.util.ExceptionUtil;
import org.mimicry.util.concurrent.DefaultFuture;
import org.mimicry.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * This class manages all threads created within a single simulated application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ThreadManager
{
    /**
     * Creates an empty thread manager without any attached {@link IManagedThread}s.
     * 
     * @param appId
     */
    public ThreadManager(UUID appId, ThreadScheduler scheduler)
    {
        Preconditions.checkNotNull(appId);
        Preconditions.checkNotNull(scheduler);

        this.appId = appId;
        this.scheduler = scheduler;

        threads = new ArrayList<IManagedThread>();
        shutdownFuture = new DefaultFuture();
    }

    /**
     * Invoked by {@link IManagedThread} in the constructor.
     * 
     * @param thread
     */
    public void threadCreated(IManagedThread thread)
    {
        synchronized (threads)
        {
            threads.add(thread);
            scheduler.threadCreated(thread);
        }
    }

    /**
     * Invoked if a thread terminates normally.
     * 
     * @param thread
     *            The thread which terminated.
     */
    public void threadTerminated(IManagedThread thread)
    {
        synchronized (threads)
        {
            threads.remove(thread);
            scheduler.threadTerminated(thread);
            if (threads.size() == 0)
            {
                shutdownFuture.setSuccess();
            }
        }
    }

    /**
     * Invoked if a thread terminates due to an exception.
     * 
     * @param thread
     *            The thread which terminated.
     * @param th
     *            The reason for thread termination.
     */
    public void threadTerminated(IManagedThread thread, Throwable th)
    {
        if (ExceptionUtil.exceptionWasCausedByThreadDeath(th))
        {
            logger.info("Thread '" + thread.getName() + "' has been stopped by Mimicry.");
        }
        else
        {
            logger.info("Thread '" + thread.getName() + "' terminated due to exception.", th);
        }
        synchronized (threads)
        {
            threads.remove(thread);
            scheduler.threadTerminated(thread);
            if (threads.size() == 0)
            {
                shutdownFuture.setSuccess();
            }
        }
    }

    /**
     * Returns the id of the application this thread belongs to.
     * 
     * @return The application's id this manager belongs to.
     */
    public UUID getApplicationId()
    {
        return appId;
    }

    /**
     * Returns the shutdown future that is triggered once all threads added to this instance have been terminated.
     * 
     * @return
     */
    public Future<?> getShutdownFuture()
    {
        return shutdownFuture;
    }

    /**
     * Triggers the shutdown procedure of all attached threads and returns the shutdown future that is activated once
     * all threads have been terminated.
     * 
     * @return
     */
    public Future<?> shutdownAllThreads()
    {
        synchronized (threads)
        {
            for (IManagedThread thread : threads)
            {
                if (thread != null)
                {
                    thread.shutdownGracefully();
                }
            }
        }
        return shutdownFuture;
    }

    public ThreadScheduler getScheduler()
    {
        return scheduler;
    }

    private final ThreadScheduler scheduler;
    private final List<IManagedThread> threads;
    private final UUID appId;
    private final Future<?> shutdownFuture;
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ThreadManager.class);
    }
}
