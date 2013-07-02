package com.gc.mimicry.bridge.cflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.shared.events.BaseEvent;
import com.gc.mimicry.shared.events.Event;

public class CFlowManager implements EventListener
{
    private final Map<UUID, ControlFlow> controlFlows;
    private final UUID appId;
    private final EventBridge bridge;
    private final Map<Class<? extends Event>, List<EventListener>> listener;

    public CFlowManager(UUID appId, EventBridge bridge)
    {
        this.appId = appId;
        this.bridge = bridge;

        listener = new HashMap<Class<? extends Event>, List<EventListener>>();

        controlFlows = new HashMap<UUID, ControlFlow>();
        bridge.addUpstreamEventListener(appId, this);
    }

    @Override
    public void handleEvent(Event evt)
    {
        UUID cflowId = evt.getAssociatedControlFlow();
        if (cflowId != null)
        {
            ControlFlow controlFlow = controlFlows.remove(cflowId);
            if (controlFlow != null)
            {
                controlFlow.terminate(evt);
            }
        }
        List<EventListener> list = listener.get(evt.getClass());
        if (list != null)
        {
            for (EventListener l : list)
            {
                l.handleEvent(evt);
            }
        }
    }

    public void terminateAll(Event evt)
    {
        Collection<ControlFlow> values = controlFlows.values();
        for (ControlFlow cflow : values)
        {
            cflow.terminate(evt);
        }
        controlFlows.clear();
    }

    public ControlFlow createControlFlow(BaseEvent event)
    {
        ControlFlow cflow = new ControlFlow();
        controlFlows.put(cflow.getId(), cflow);
        event.setControlFlowId(cflow.getId());
        event.setSourceApp(appId);
        bridge.dispatchEventToStack(event);
        return cflow;
    }

    public void addHandler(Class<? extends Event> eventClass, EventListener l)
    {
        List<EventListener> eventListener = listener.get(eventClass);
        if (eventListener == null)
        {
            eventListener = new ArrayList<EventListener>();
        }
        eventListener.add(l);
        listener.put(eventClass, eventListener);
    }
}
