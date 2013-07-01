package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.Event;

/**
 * The event broker dispatches events between the {@link EventStack} of different nodes. It can also be used to trigger
 * events from within the simulation script.
 * 
 * @author Marc-Christian schulze
 * 
 */
public interface EventBroker
{
    public void fireEvent(Event event);

    public void fireEvent(Event event, EventListener ignoreListener);

    public void addEventListener(EventListener l);

    public void removeEventListener(EventListener l);
}
