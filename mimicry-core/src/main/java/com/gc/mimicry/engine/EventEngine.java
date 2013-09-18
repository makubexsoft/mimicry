package com.gc.mimicry.engine;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.EventStack;

/**
 * The event broker dispatches events between the {@link EventStack} of different nodes. It can also be used to trigger
 * events from within the simulation script.
 * 
 * @author Marc-Christian schulze
 * 
 */
public interface EventEngine
{
    public void fireEvent(Event event);

    public void fireEvent(Event event, EventListener ignoreListener);

    public void addEventListener(EventListener l);

    public void removeEventListener(EventListener l);
}
