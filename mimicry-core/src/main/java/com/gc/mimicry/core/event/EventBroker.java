package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.Event;

public interface EventBroker
{
    public void fireEvent(Event event);

    public void addEventListener(EventListener l);

    public void removeEventListener(EventListener l);
}
