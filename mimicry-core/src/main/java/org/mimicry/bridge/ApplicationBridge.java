package org.mimicry.bridge;

import java.lang.reflect.Method;

import org.mimicry.bridge.threading.ThreadManager;
import org.mimicry.engine.stack.EventBridge;
import org.mimicry.engine.timing.Timeline;

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
            setTimelineMethod = bridgeClass.getDeclaredMethod(SET_TIMELINE_METHOD_NAME, Timeline.class);
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

    public void setTimeline(Timeline c)
    {
        Preconditions.checkNotNull(c);
        try
        {
            setTimelineMethod.invoke(null, c);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to set timeline.", e);
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
    private static final String SET_TIMELINE_METHOD_NAME;
    private static final String SET_EVENT_BRIDGE_METHOD_NAME;
    static
    {
        BRIDGE_CLASS_NAME = "org.mimicry.bridge.SimulatorBridge";
        SET_ID_METHOD_NAME = "setThreadManager";
        SET_TIMELINE_METHOD_NAME = "setTimeline";
        SET_EVENT_BRIDGE_METHOD_NAME = "setEventBridge";
    }

    private Class<?> bridgeClass;
    private Method setIdMethod;
    private Method setTimelineMethod;
    private Method setEventBridgeMethod;
}
