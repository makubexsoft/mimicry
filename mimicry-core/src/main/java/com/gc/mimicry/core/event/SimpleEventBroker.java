package com.gc.mimicry.core.event;

import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleEventBroker implements EventBroker
{
    private final CopyOnWriteArrayList<EventListener> listener;

    public SimpleEventBroker()
    {
        listener = new CopyOnWriteArrayList<EventListener>();
    }

    @Override
    public void fireEvent(Event event)
    {
        for (EventListener l : listener)
        {
            l.eventOccurred(event);
        }
    }

    @Override
    public void addEventListener(EventListener l)
    {
        listener.add(l);
    }

    @Override
    public void removeEventListener(EventListener l)
    {
        listener.remove(l);
    }
}
