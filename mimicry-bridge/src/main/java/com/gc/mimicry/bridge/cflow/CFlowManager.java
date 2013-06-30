package com.gc.mimicry.bridge.cflow;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.event.Event;
import com.gc.mimicry.core.event.EventListener;

public class CFlowManager implements EventListener
{
    private final Map<UUID, ControlFlow> controlFlows;

    public CFlowManager()
    {
        controlFlows = new HashMap<UUID, ControlFlow>();
    }

    @Override
    public void eventOccurred(Event evt)
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
