package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.Event;

/**
 * Implement this interface to register yourself as listener to the {@link EventBroker}.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface EventListener
{
    /**
     * Invoked when an event has occurred.
     * 
     * @param evt
     */
    public void handleEvent(Event evt);
}
