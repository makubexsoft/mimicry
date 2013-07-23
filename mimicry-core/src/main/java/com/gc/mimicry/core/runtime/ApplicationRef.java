package com.gc.mimicry.core.runtime;

import java.io.Serializable;
import java.util.UUID;

import com.gc.mimicry.util.concurrent.Future;

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

    /**
     * Starts the main thread of the application if not already done. Multiple invocations are ignored.
     */
    public void start(String... commandArgs);

    /**
     * Initiates a shutdown of the application and returns a future for observing it.
     * 
     * @return
     */
    public Future<?> stop();
}
