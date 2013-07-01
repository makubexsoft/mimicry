package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.Event;

public interface EventListener
{
    public void handleEvent(Event evt);
}
