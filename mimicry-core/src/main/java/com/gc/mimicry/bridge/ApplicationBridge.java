package com.gc.mimicry.bridge;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * The application bridge is created for each simulated application instance. It provides reflective access to the
 * address space of the simulated application instance.
 * 
 * @author Marc-Christian Schulze
 */
public class ApplicationBridge
{
    /**
     * Creates a new bridge to the application loaded by the given {@link ClassLoader}.
     * 
     * @param classLoader
     *            The class loader used to load the application.
     * @throws BridgeNotFoundException
     *             If the given class loader can't find/access the bridge or the bridge doesn't provide the required
     *             methods.
     * @throws NullPointerException
     *             If classLoader is null.
     */
    public ApplicationBridge(ClassLoader classLoader) throws BridgeNotFoundException
    {
        Preconditions.checkNotNull(classLoader);

        this.classLoader = classLoader;

        try
        {
            bridgeClass = classLoader.loadClass(BRIDGE_CLASS_NAME);
            setIdMethod = bridgeClass.getDeclaredMethod(SET_ID_METHOD_NAME, UUID.class);
            setClockMethod = bridgeClass.getDeclaredMethod(SET_CLOCK_METHOD_NAME, Clock.class);
            setEventBridgeMethod = bridgeClass.getDeclaredMethod(SET_EVENT_BRIDGE_METHOD_NAME, EventBridge.class);
            shutdownMethod = bridgeClass.getDeclaredMethod(SHUTDOWN_METHOD_NAME);
            startMethod = bridgeClass.getDeclaredMethod(START_METHOD_NAME, ClassLoader.class);
            setMainClassMethod = bridgeClass.getDeclaredMethod(SET_MAIN_CLASS_METHOD_NAME, String.class);
            setCommandArgsMethod = bridgeClass.getDeclaredMethod(SET_COMMANDARGS_METHOD_NAME, Set.class);
            getShutdownFutureMethod = bridgeClass.getDeclaredMethod(GET_SHUTDOWN_FUTURE_METHOD_NAME);
        }
        catch (Exception e)
        {
            throw new BridgeNotFoundException("Can't find/access simulator bridge.", e);
        }
    }

    public void setCommandArgs(Set<String> args)
    {
        Preconditions.checkNotNull(args);
        try
        {
            setCommandArgsMethod.invoke(null, args);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set args: " + args, e);
        }
    }

    public void setMainClass(String className)
    {
        Preconditions.checkNotNull(className);
        try
        {
            setMainClassMethod.invoke(null, className);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set main class: " + className, e);
        }
    }

    public void setApplicationId(UUID id)
    {
        Preconditions.checkNotNull(id);
        try
        {
            setIdMethod.invoke(null, id);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set application id.", e);
        }
    }

    public void shutdownApplication()
    {
        try
        {
            shutdownMethod.invoke(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to shutdown application.", e);
        }
    }

    public void startApplication()
    {
        try
        {
            startMethod.invoke(null, classLoader);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to start application.", e);
        }
    }

    public void setClock(Clock c)
    {
        Preconditions.checkNotNull(c);
        try
        {
            setClockMethod.invoke(null, c);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set clock.", e);
        }
    }

    public void setEventBridge(EventBridge bridge)
    {
        Preconditions.checkNotNull(bridge);
        try
        {
            setEventBridgeMethod.invoke(null, bridge);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set event bridge.", e);
        }
    }

    public Future<?> getShutdownFuture()
    {
        try
        {
            return (Future<?>) getShutdownFutureMethod.invoke(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to obtain shutdown future.", e);
        }
    }

    private static final String BRIDGE_CLASS_NAME;
    private static final String SET_ID_METHOD_NAME;
    private static final String SET_CLOCK_METHOD_NAME;
    private static final String SET_EVENT_BRIDGE_METHOD_NAME;
    private static final String SHUTDOWN_METHOD_NAME;
    private static final String START_METHOD_NAME;
    private static final String SET_MAIN_CLASS_METHOD_NAME;
    private static final String SET_COMMANDARGS_METHOD_NAME;
    private static final String GET_SHUTDOWN_FUTURE_METHOD_NAME;
    static
    {
        BRIDGE_CLASS_NAME = "com.gc.mimicry.bridge.SimulatorBridge";
        SET_ID_METHOD_NAME = "setApplicationId";
        SET_CLOCK_METHOD_NAME = "setClock";
        SET_EVENT_BRIDGE_METHOD_NAME = "setEventBridge";
        SHUTDOWN_METHOD_NAME = "shutdownApplication";
        START_METHOD_NAME = "startApplication";
        SET_MAIN_CLASS_METHOD_NAME = "setMainClass";
        SET_COMMANDARGS_METHOD_NAME = "setCommandArgs";
        GET_SHUTDOWN_FUTURE_METHOD_NAME = "getShutdownFuture";
    }

    private Class<?> bridgeClass;
    private Method setIdMethod;
    private Method setClockMethod;
    private Method setEventBridgeMethod;
    private Method shutdownMethod;
    private Method startMethod;
    private Method setMainClassMethod;
    private Method setCommandArgsMethod;
    private Method getShutdownFutureMethod;
    private final ClassLoader classLoader;
}
