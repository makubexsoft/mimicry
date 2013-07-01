package com.gc.mimicry.bridge;

import java.util.UUID;

import com.gc.mimicry.shared.events.Event;

public class Bridge
{
    public static void emitEvent(Event evt)
    {
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
    }

    public static UUID appId()
    {
        return SimulatorBridge.getApplicationId();
    }
}
