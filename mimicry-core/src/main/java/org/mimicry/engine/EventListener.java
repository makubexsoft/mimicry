package org.mimicry.engine;

import org.mimicry.engine.event.ApplicationEvent;


/**
 * Implement this interface to register yourself as listener to the {@link EventEngine}.
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
    public void handleEvent(ApplicationEvent evt);
}
