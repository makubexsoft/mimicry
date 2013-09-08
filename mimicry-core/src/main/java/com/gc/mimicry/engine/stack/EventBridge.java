package com.gc.mimicry.engine.stack;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.events.SetApplicationActiveEvent;

/**
 * This bridge is placed on top of the {@link EventStack} and below all applications. It basically routes and filters
 * events for the applications above.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventBridge
{
    private final Map<UUID, Boolean> applicationActiveState;
    private final CopyOnWriteArrayList<EventListener> downstreamListener;
    private final Map<UUID, CopyOnWriteArrayList<WeakReference<EventListener>>> upstreamListener;

    public EventBridge()
    {
        applicationActiveState = new HashMap<UUID, Boolean>();
        downstreamListener = new CopyOnWriteArrayList<EventListener>();
        upstreamListener = new HashMap<UUID, CopyOnWriteArrayList<WeakReference<EventListener>>>();
    }

    /**
     * Passes the event to the target application if specified and the application is active.
     * 
     * @param evt
     */
    public void dispatchEventToApplication(Event evt)
    {
        if (evt instanceof SetApplicationActiveEvent)
        {
            setApplicationActive(evt.getSourceApplication(), ((SetApplicationActiveEvent) evt).isActive());
        }
        else
        {
            handleEvent(evt);
        }
    }

    public void dispatchEventToStack(Event evt)
    {
        if (downstreamListener.size() == 0)
        {
            System.out.println("WARNING: no receiver for " + evt);
        }
        for (EventListener l : downstreamListener)
        {
            l.handleEvent(evt);
        }
    }

    public void addDownstreamEventListener(EventListener l)
    {
        downstreamListener.add(l);
    }

    public void removeDownstreamEventListener(EventListener l)
    {
        downstreamListener.remove(l);
    }

    public void addUpstreamEventListener(UUID applicationId, EventListener l)
    {
        CopyOnWriteArrayList<WeakReference<EventListener>> list = upstreamListener.get(applicationId);
        if (list == null)
        {
            list = new CopyOnWriteArrayList<WeakReference<EventListener>>();
        }
        list.add(new WeakReference<EventListener>(l));
        upstreamListener.put(applicationId, list);
    }

    public void removeUpstreamEventListener(UUID applicationId, EventListener l)
    {
        CopyOnWriteArrayList<WeakReference<EventListener>> list = upstreamListener.get(applicationId);
        if (list != null)
        {
            list.remove(l);
        }
    }

    private void handleEvent(Event evt)
    {
        UUID targetApplication = evt.getTargetApplication();

        if (targetApplication == null)
        {
            return;
        }
        if (!isApplicationActive(targetApplication))
        {
            return;
        }

        List<WeakReference<EventListener>> list = upstreamListener.get(targetApplication);
        if (list == null)
        {
            return;
        }

        for (WeakReference<EventListener> ref : list)
        {
            EventListener listener = ref.get();
            if (listener != null)
            {
                listener.handleEvent(evt);
            }
            else
            {
                list.remove(ref);
            }
        }
    }

    private boolean isApplicationActive(UUID appId)
    {
        Boolean flag = applicationActiveState.get(appId);
        if (flag == null)
        {
            return true;
        }
        return flag;
    }

    private void setApplicationActive(UUID appId, boolean active)
    {
        applicationActiveState.put(appId, active);
    }
}
