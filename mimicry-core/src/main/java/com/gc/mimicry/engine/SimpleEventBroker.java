package com.gc.mimicry.engine;

import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.EventHandler;

/**
 * A simple in-memory implementation of an event broker that is used for standalone and unit-test setups.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class SimpleEventBroker implements EventEngine
{
    private final CopyOnWriteArrayList<EventListener> listener;

    /**
     * Constructs a new broker without any listeners attached to it.
     */
    public SimpleEventBroker()
    {
        listener = new CopyOnWriteArrayList<EventListener>();
    }

    /**
     * Dispatches the given event to all {@link EventListener} attached to this broker.
     */
    @Override
    public void fireEvent(Event event)
    {
        for (EventListener l : listener)
        {
            l.handleEvent(event);
        }
    }

    /**
     * Dispatches the given event to all {@link EventListener} attached to this broker except the given
     * <i>ignoreListener</i>.
     */
    @Override
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

    /**
     * Attaches an {@link EventListener} to this broker in order to receive events.
     */
    @Override
    public void addEventListener(EventListener l)
    {
        listener.add(l);
    }

    /**
     * Detaches the given {@link EventHandler} from this broker.
     */
    @Override
    public void removeEventListener(EventListener l)
    {
        listener.remove(l);
    }
}
