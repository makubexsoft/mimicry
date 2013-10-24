package com.gc.mimicry.engine.local;

import java.io.Closeable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.bridge.threading.ThreadScheduler;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.streams.ApplicationHasBeenStartedStream;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * A simulated application running within the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LocalApplication implements Closeable, Application
{
    private final UUID id;
    private final EntryPoint entryPoint;
    private final ApplicationBridge bridge;
    private final ThreadManager threadManager;
    private final CEPEngine eventEngine;
    private final Timeline timeline;

    public LocalApplication(ApplicationContext ctx, EntryPoint entryPoint, ThreadScheduler scheduler, CEPEngine siddhi)
    {
        Preconditions.checkNotNull(ctx);
        Preconditions.checkNotNull(entryPoint);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(siddhi);

        this.entryPoint = entryPoint;
        this.eventEngine = siddhi;

        id = UUID.randomUUID();
        threadManager = new ThreadManager(id, scheduler);

        timeline = ctx.getTimeline();

        bridge = new ApplicationBridge(ctx.getClassLoader());
        bridge.setTimeline(ctx.getTimeline());
        bridge.setEventBridge(ctx.getEventBridge());
        bridge.setThreadManager(threadManager);
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public void start(String... args)
    {
        try
        {
            entryPoint.main(args);

            ApplicationHasBeenStartedStream.get(eventEngine).send(timeline.currentMillis(), getId());
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Failed to start application.", e);
        }
    }

    @Override
    public Future<?> getTerminationFuture()
    {
        return threadManager.getShutdownFuture();
    }

    @Override
    public Future<?> stop()
    {
        return threadManager.shutdownAllThreads();
    }

    @Override
    public void close()
    {
        stop().awaitUninterruptibly(10, TimeUnit.SECONDS);
    }
}
