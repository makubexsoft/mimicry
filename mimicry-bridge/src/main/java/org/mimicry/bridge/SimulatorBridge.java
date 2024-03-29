package org.mimicry.bridge;

import java.io.InputStream;
import java.util.UUID;

import org.mimicry.EventListener;
import org.mimicry.bridge.ApplicationBridge;
import org.mimicry.bridge.threading.ThreadManager;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventBridge;
import org.mimicry.events.stdio.ConsoleStdinEvent;
import org.mimicry.timing.Timeline;
import org.mimicry.util.ByteBuffer;


/**
 * The simulator bridge provides access for the aspects woven into the application code to the simulator that loaded the
 * application. It should not be used by application code!
 * 
 * @author Marc-Christian Schulze
 * @see ApplicationBridge
 */
public final class SimulatorBridge
{
    private static UUID applicationId;
    private static Timeline timeline;
    private static EventBridge eventBridge;
    private static ThreadManager threadManager;
    private static ClassLoader systemClassLoader;
    private static ByteBuffer inputBuffer;
    private static InputStream systemInputStream;
    private static EventListener inputHandler;

    static
    {
        inputBuffer = new ByteBuffer();
        systemInputStream = inputBuffer.createStream();
    }

    public static void setSystemInputStream(InputStream stream)
    {
        systemInputStream = stream;
    }

    public static InputStream getSystemInputStream()
    {
        return systemInputStream;
    }

    /**
     * Avoids instantiation.
     */
    private SimulatorBridge()
    {
        throw new UnsupportedOperationException();
    }

    public static ClassLoader getSystemClassLoader()
    {
        return systemClassLoader;
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setThreadManager(ThreadManager mgr)
    {
        applicationId = mgr.getApplicationId();
        threadManager = mgr;
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setTimeline(Timeline timeline)
    {
        SimulatorBridge.timeline = timeline;
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setEventBridge(EventBridge eventBridge)
    {
        SimulatorBridge.eventBridge = eventBridge;

        inputHandler = new EventListener()
        {

            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                if (evt instanceof ConsoleStdinEvent)
                {
                    ConsoleStdinEvent cie = (ConsoleStdinEvent) evt;
                    inputBuffer.write(cie.getData());
                }
            }
        };
        eventBridge.addUpstreamEventListener(getApplicationId(), inputHandler);
    }

    /**
     * Returns the unique id assigned to this application instance.
     * 
     * @return The unique id assigned to this application instance.
     */
    public static UUID getApplicationId()
    {
        return applicationId;
    }

    public static Timeline getTimeline()
    {
        return timeline;
    }

    public static EventBridge getEventBridge()
    {
        if (eventBridge == null)
        {
            throw new IllegalStateException("Not initialized. eventBridge is missing.");
        }
        return eventBridge;
    }

    public static ThreadManager getThreadManager()
    {
        if (threadManager == null)
        {
            throw new IllegalStateException("Not initialized. Thread manager is missing.");
        }
        return threadManager;
    }

    /**
     * Gets invoked when the application wants to shutdown itself, e.g. {@link System#exit()} has been invoked, as well
     * as by the {@link ApplicationBridge}.
     */
    public static void shutdownApplication()
    {
        getThreadManager().shutdownAllThreads();
    }
}
