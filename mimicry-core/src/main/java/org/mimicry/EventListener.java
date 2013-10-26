package org.mimicry;

import org.mimicry.engine.ApplicationEvent;


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
