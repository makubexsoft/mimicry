package org.mimicry.engine.stack;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.engine.EventListener;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.stack.events.SetApplicationActiveEvent;
import org.mimicry.engine.streams.ApplicationEventStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

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
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final CEPEngine eventEngine;

    public EventBridge(CEPEngine eventEngine)
    {
        Preconditions.checkNotNull(eventEngine);
        this.eventEngine = eventEngine;

        applicationActiveState = new HashMap<UUID, Boolean>();
        downstreamListener = new CopyOnWriteArrayList<EventListener>();
        upstreamListener = new HashMap<UUID, CopyOnWriteArrayList<WeakReference<EventListener>>>();
    }

    /**
     * Passes the event to the target application if specified and the application is active.
     * 
     * @param evt
     */
    public void dispatchEventToApplication(ApplicationEvent evt)
    {
        recordEvent(Direction.UPSTREAM, evt);

        if (evt instanceof SetApplicationActiveEvent)
        {
            setApplicationActive(evt.getApplication(), ((SetApplicationActiveEvent) evt).isActive());
        }
        else
        {
            handleEvent(evt);
        }
    }

    public void dispatchEventToStack(ApplicationEvent evt)
    {
        recordEvent(Direction.DOWNSTREAM, evt);

        if (downstreamListener.size() == 0)
        {
            System.out.println("WARNING: no receiver for " + evt);
        }
        for (EventListener l : downstreamListener)
        {
            l.handleEvent(evt);
        }
    }

    private void recordEvent(Direction direction, ApplicationEvent event)
    {
        try
        {
            String eventAsJson = jsonMapper.writeValueAsString(event);
            Stream stream = ApplicationEventStream.get(eventEngine);
            String cflowId = "";
            if (event.getControlFlow() != null)
            {
                cflowId = event.getControlFlow().toString();
            }
            stream.send(event.getApplication().toString(), cflowId, direction.toString(), eventAsJson);
        }
        catch (JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    private void handleEvent(ApplicationEvent evt)
    {
        UUID targetApplication = evt.getApplication();
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

enum Direction
{
    UPSTREAM, DOWNSTREAM
}