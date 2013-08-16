package com.gc.mimicry.bridge;

import java.lang.reflect.Method;

import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Clock;
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

        try
        {
            bridgeClass = classLoader.loadClass(BRIDGE_CLASS_NAME);
            setIdMethod = bridgeClass.getDeclaredMethod(SET_ID_METHOD_NAME, ThreadManager.class);
            setClockMethod = bridgeClass.getDeclaredMethod(SET_CLOCK_METHOD_NAME, Clock.class);
            setEventBridgeMethod = bridgeClass.getDeclaredMethod(SET_EVENT_BRIDGE_METHOD_NAME, EventBridge.class);
        }
        catch (Exception e)
        {
            throw new BridgeNotFoundException("Can't find/access simulator bridge.", e);
        }
    }

    public void setThreadManager(ThreadManager mgr)
    {
        Preconditions.checkNotNull(mgr);
        try
        {
            setIdMethod.invoke(null, mgr);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set thread manager.", e);
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

    private static final String BRIDGE_CLASS_NAME;
    private static final String SET_ID_METHOD_NAME;
    private static final String SET_CLOCK_METHOD_NAME;
    private static final String SET_EVENT_BRIDGE_METHOD_NAME;
    static
    {
        BRIDGE_CLASS_NAME = "com.gc.mimicry.bridge.SimulatorBridge";
        SET_ID_METHOD_NAME = "setThreadManager";
        SET_CLOCK_METHOD_NAME = "setClock";
        SET_EVENT_BRIDGE_METHOD_NAME = "setEventBridge";
    }

    private Class<?> bridgeClass;
    private Method setIdMethod;
    private Method setClockMethod;
    private Method setEventBridgeMethod;
}
