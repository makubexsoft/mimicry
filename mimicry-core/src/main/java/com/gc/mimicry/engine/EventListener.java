package com.gc.mimicry.engine;

import com.gc.mimicry.engine.event.Event;


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
