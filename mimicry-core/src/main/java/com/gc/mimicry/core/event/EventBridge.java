package com.gc.mimicry.core.event;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.shared.events.Event;

/**
 * 
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

    public void dispatchEventToApplication(Event evt)
    {
        if (evt instanceof SetApplicationActiveEvent)
        {
            setApplicationActive(evt.getDestinationAppId(), ((SetApplicationActiveEvent) evt).isActive());
        }
        else
        {
            handleEvent(evt);
        }
    }

    private void handleEvent(Event evt)
    {
        UUID appId = evt.getDestinationAppId();
        if (isApplicationActive(appId))
        {
            CopyOnWriteArrayList<WeakReference<EventListener>> list = upstreamListener.get(evt.getDestinationAppId());
            if (list != null)
            {
                for (WeakReference<EventListener> ref : list)
                {
                    EventListener listener = ref.get();
                    if (listener != null)
                    {
                        listener.eventOccurred(evt);
                    }
                    else
                    {
                        list.remove(ref);
                    }
                }
            }
        }
    }

    public void dispatchEventToStack(Event evt)
    {
        for (EventListener l : downstreamListener)
        {
            l.eventOccurred(evt);
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
