package com.gc.mimicry.core.runtime;

import java.io.Serializable;
import java.util.UUID;

/**
 * Serializable application reference used to refer to applications across JVMs.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ApplicationRef extends Serializable
{
    /**
     * Returns the node id the application is launched on.
     * 
     * @return
     */
    public UUID getNodeId();

    /**
     * Returns the id of the application.
     * 
     * @return
     */
    public UUID getApplicationId();
}
