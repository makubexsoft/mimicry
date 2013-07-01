package com.gc.mimicry.bridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.bridge.threading.ThreadShouldTerminateException;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.util.concurrent.Future;

/**
 * The simulator bridge provides access for the aspects woven into the application code to the simulator that loaded the
 * application. It should not be used by application code!
 * 
 * @author Marc-Christian Schulze
 * @see ApplicationBridge
 */
public final class SimulatorBridge
{
    private static volatile boolean started;
    private static UUID applicationId;
    private static Clock clock;
    private static EventBridge eventBridge;
    private static ThreadManager threadManager;
    private static String mainClassName;
    private static Set<String> commandArgs;

    /**
     * Avoids instantiation.
     */
    private SimulatorBridge()
    {
        throw new UnsupportedOperationException();
    }

    public static void setMainClass(String mainClassName)
    {
        SimulatorBridge.mainClassName = mainClassName;
    }

    public static void setCommandArgs(Set<String> args)
    {
        commandArgs = args;
    }

    public static boolean wasStarted()
    {
        return started;
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setApplicationId(UUID id)
    {
        applicationId = id;
        threadManager = new ThreadManager(id);
    }

    public static Future<?> getShutdownFuture()
    {
        checkInitialized();
        return threadManager.getShutdownFuture();
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setClock(Clock clock)
    {
        SimulatorBridge.clock = clock;
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     */
    public static void setEventBridge(EventBridge eventBridge)
    {
        SimulatorBridge.eventBridge = eventBridge;
    }

    private static void checkInitialized()
    {
        if (mainClassName == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. Main-Class missing.");
        }
        if (commandArgs == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. Command arguments missing.");
        }
        if (applicationId == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. Application id missing.");
        }
        if (threadManager == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. Thread manager missing.");
        }
        if (clock == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. Clock missing.");
        }
        if (eventBridge == null)
        {
            throw new IllegalStateException("Bridge not fully initialized. EventBridge missing.");
        }
    }

    /**
     * Gets invoked by the {@link ApplicationBridge}.
     * 
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void startApplication(ClassLoader applicationLoader) throws ClassNotFoundException,
            NoSuchMethodException, SecurityException, NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException
    {
        checkInitialized();
        if (started)
        {
            throw new IllegalStateException("Application already running.");
        }

        Class<?> mainClass = applicationLoader.loadClass(mainClassName);
        final Method mainMethod = mainClass.getMethod("main", String[].class);
        ManagedThread thread = new ManagedThread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    mainMethod.invoke(null, new Object[] { commandArgs.toArray(new String[0]) });
                }
                catch (IllegalAccessException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    if (e.getCause() instanceof ThreadShouldTerminateException)
                    {
                        // application shut down by our life-cycle
                    }
                    else
                    {
                        // application crashed due to unknown exception
                        e.printStackTrace();
                    }
                }

            }
        }, "Application[id=" + getApplicationId() + "] main");
        thread.start();
        started = true;
    }

    /**
     * Returns the unique id assigned to this application instance.
     * 
     * @return The unique id assigned to this application instance.
     */
    public static UUID getApplicationId()
    {
        checkInitialized();
        return applicationId;
    }

    public static Clock getClock()
    {
        checkInitialized();
        return clock;
    }

    public static EventBridge getEventBridge()
    {
        checkInitialized();
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
        if (!started)
        {
            throw new IllegalStateException("Application not started.");
        }
        getThreadManager().shutdownAllThreads();
    }
}
