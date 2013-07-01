package com.gc.mimicry.core.event;

import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.shared.events.Event;

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
            l.handleEvent(event);
        }
    }

    public void fireEvent(Event event, EventListener ignoreListener)
    {
        for (EventListener l : listener)
        {
            if (l != ignoreListener)
            {
                l.handleEvent(event);
            }
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
