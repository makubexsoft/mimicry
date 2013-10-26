package org.mimicry.engine;

import java.io.Serializable;
import java.util.UUID;

import org.mimicry.util.VectorClock;


/**
 * Basic interface for all events of the system.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ApplicationEvent extends Serializable
{
    /**
     * Returns the logical clock that was used to create this event.
     * 
     * @return
     */
    public VectorClock<UUID> getClock();

    /**
     * Returns the id of the associated control flow or null if no control flow is associated.
     * 
     * @return
     */
    public UUID getControlFlow();

    /**
     * Returns the id of the application which caused this event or null if this event was not caused by an application.
     * 
     * @return
     */
    public UUID getApplication();
}
