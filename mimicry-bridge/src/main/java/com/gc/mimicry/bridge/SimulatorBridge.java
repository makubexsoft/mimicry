package com.gc.mimicry.bridge;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.bridge.threading.ThreadShouldTerminateException;
import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Clock;
import com.gc.mimicry.ext.stdio.events.ConsoleInputEvent;
import com.gc.mimicry.util.ByteBuffer;
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
    private static List<String> commandArgs;
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

    public static void setMainClass(String mainClassName)
    {
        SimulatorBridge.mainClassName = mainClassName;
    }

    public static void setCommandArgs(List<String> args)
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
    public static void setThreadManager(ThreadManager mgr)
    {
        applicationId = mgr.getApplicationId();
        threadManager = mgr;
    }

    public static Future<?> getShutdownFuture()
    {
        if (threadManager == null)
        {
            throw new IllegalStateException("No thread manager in place. Invoke setApplicationId first.");
        }
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

        systemClassLoader = applicationLoader;

        inputHandler = new EventListener()
        {

            @Override
            public void handleEvent(Event evt)
            {
                if (evt instanceof ConsoleInputEvent)
                {
                    ConsoleInputEvent cie = (ConsoleInputEvent) evt;
                    inputBuffer.write(cie.getData());
                }
            }
        };
        eventBridge.addUpstreamEventListener(getApplicationId(), inputHandler);

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
        thread.setContextClassLoader(applicationLoader);
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
        return applicationId;
    }

    public static Clock getClock()
    {
        return clock;
    }

    public static EventBridge getEventBridge()
    {
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
