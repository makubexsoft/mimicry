package org.mimicry.bridge.cflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mimicry.EventListener;
import org.mimicry.bridge.ControlFlow;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventBridge;


public class CFlowManager implements EventListener
{
    private final Map<UUID, ControlFlow> controlFlows;
    private final UUID appId;
    private final EventBridge bridge;
    private final Map<Class<? extends ApplicationEvent>, List<EventListener>> listener;

    public CFlowManager(UUID appId, EventBridge bridge)
    {
        this.appId = appId;
        this.bridge = bridge;

        listener = new HashMap<Class<? extends ApplicationEvent>, List<EventListener>>();

        controlFlows = new HashMap<UUID, ControlFlow>();
        bridge.addUpstreamEventListener(appId, this);
    }

    @Override
    public void handleEvent(ApplicationEvent evt)
    {
        UUID cflowId = evt.getControlFlow();
        if (cflowId != null)
        {
            ControlFlow controlFlow = controlFlows.remove(cflowId);
            if (controlFlow != null)
            {
                controlFlow.terminate(evt);
            }
        }
        for (Map.Entry<Class<? extends ApplicationEvent>, List<EventListener>> entry : listener.entrySet())
        {
            if (entry.getKey().isAssignableFrom(evt.getClass()))
            {
                List<EventListener> list = entry.getValue();
                if (list != null)
                {
                    for (EventListener l : list)
                    {
                        l.handleEvent(evt);
                    }
                }
            }
        }
    }

    public void terminateAll(ApplicationEvent evt)
    {
        Collection<ControlFlow> values = controlFlows.values();
        for (ControlFlow cflow : values)
        {
            cflow.terminate(evt);
        }
        controlFlows.clear();
    }

    public ControlFlow createControlFlow()
    {
        ControlFlow cflow = new ControlFlow();
        controlFlows.put(cflow.getId(), cflow);
        return cflow;
    }

    public void addHandler(Class<? extends ApplicationEvent> eventClass, EventListener l)
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
