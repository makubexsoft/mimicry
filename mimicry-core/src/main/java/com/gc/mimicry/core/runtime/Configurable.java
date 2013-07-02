package com.gc.mimicry.core.runtime;

import java.util.Map;

import com.gc.mimicry.core.event.EventHandler;
import com.gc.mimicry.core.event.EventStack;

/**
 * Implement this interface by your {@link EventHandler} to allow configuration using the
 * {@link EventHandlerConfiguration}.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface Configurable
{
    /**
     * Invoked after the event handler has been created but before it's attached to the {@link EventStack} and before it
     * gets initialized.
     * 
     * @param configuration
     *            The configuration as specified in the {@link EventHandlerConfiguration} set up in the simulation
     *            script.
     */
    public void configure(Map<String, String> configuration);
}
