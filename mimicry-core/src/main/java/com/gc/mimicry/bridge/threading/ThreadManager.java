package com.gc.mimicry.bridge.threading;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * This class is used as registry for all managed threads created.
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

        threads = new CopyOnWriteArrayList<IManagedThread>();
        shutdownFuture = new DefaultFuture();
    }

    /**
     * Invoked by {@link IManagedThread} in the constructor.
     * 
     * @param thread
     */
    public void threadCreated(IManagedThread thread)
    {
        threads.add(thread);
        scheduler.threadCreated(thread);
    }

    /**
     * Invoked if a thread terminates normally.
     * 
     * @param thread
     */
    public void threadTerminated(IManagedThread thread)
    {
        threads.remove(thread);
        scheduler.threadTerminated(thread);
        if (threads.size() == 0)
        {
            shutdownFuture.setSuccess();
        }
    }

    /**
     * Invoked if a thread terminates due to an exception.
     * 
     * @param thread
     * @param th
     */
    public void threadTerminated(IManagedThread thread, Throwable th)
    {
        if (!(th instanceof ThreadDeath))
        {
            logger.info("Thread terminated due to exception.", th);
        }
        threads.remove(thread);
        scheduler.threadTerminated(thread);
        if (threads.size() == 0)
        {
            shutdownFuture.setSuccess();
        }
    }

    /**
     * Returns the id of the application this thread belongs to.
     * 
     * @return
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
        for (IManagedThread thread : threads)
        {
            if (thread != null)
            {
                thread.shutdownGracefully();
            }
        }
        return shutdownFuture;
    }

    public ThreadScheduler getScheduler()
    {
        return scheduler;
    }

    private final ThreadScheduler scheduler;
    private final CopyOnWriteArrayList<IManagedThread> threads;
    private final UUID appId;
    private final Future<?> shutdownFuture;
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ThreadManager.class);
    }
}
