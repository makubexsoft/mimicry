package com.gc.mimicry.engine;

import java.io.Closeable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.util.concurrent.Future;

public class Application implements Closeable
{
    private final EntryPoint entryPoint;
    private final ApplicationBridge bridge;
    private final ThreadManager threadManager;

    Application(ApplicationContext ctx, EntryPoint entryPoint)
    {
        this.entryPoint = entryPoint;

        threadManager = new ThreadManager(UUID.randomUUID());

        bridge = new ApplicationBridge(ctx.getClassLoader());
        bridge.setClock(ctx.getClock());
        bridge.setEventBridge(ctx.getEventBridge());
        bridge.setThreadManager(threadManager);
    }

    public UUID getId()
    {
        return threadManager.getApplicationId();
    }

    public void start(String... args)
    {
        try
        {
            entryPoint.main(args);
        }
        catch (Throwable e)
        {
            throw new RuntimeException("Failed to start application.", e);
        }
    }

    public Future<?> getTerminationFuture()
    {
        return threadManager.getShutdownFuture();
    }

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
