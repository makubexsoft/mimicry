package com.gc.mimicry.bridge.cflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.shared.events.Event;

public class CFlowManager implements EventListener
{
    private final Map<UUID, ControlFlow> controlFlows;

    public CFlowManager(UUID appId, EventBridge bridge)
    {
        controlFlows = new HashMap<UUID, ControlFlow>();
        bridge.addUpstreamEventListener(appId, this);
    }

    @Override
    public void handleEvent(Event evt)
    {
        UUID cflowId = evt.getControlFlowId();
        if (cflowId != null)
        {
            ControlFlow controlFlow = controlFlows.remove(cflowId);
            if (controlFlow != null)
            {
                controlFlow.terminate(evt);
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

    public ControlFlow createControlFlow()
    {
        ControlFlow cflow = new ControlFlow();
        controlFlows.put(cflow.getId(), cflow);
        return cflow;
    }

    public void addHandler(Class<? extends Event> eventClass, EventListener l)
    {

    }
}
