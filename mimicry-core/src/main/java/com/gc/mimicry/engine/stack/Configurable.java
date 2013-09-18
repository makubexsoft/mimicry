package com.gc.mimicry.engine.stack;

import java.util.Map;


/**
 * Implement this interface by your {@link EventHandler} to allow configuration using the
 * {@link EventHandlerParameters}.
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
     *            The configuration as specified in the {@link EventHandlerParameters} set up in the simulation
     *            script.
     */
    public void configure(Map<String, String> configuration);
}
