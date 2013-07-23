package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.deployment.ApplicationBundleDescriptor;
import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.util.concurrent.Future;

/**
 * Basic interface for controlling a simulated network.
 * 
 * @author Marc-Christian Schulze
 */
public interface SimulatedNetwork
{
    /**
     * Initializes the network.
     * 
     * @param config
     *            The configuration to use
     */
    public void init(NetworkConfiguration config);

    /**
     * Returns the configuration with which the network was initialized or null if not yet initialized.
     * 
     * @return
     */
    public NetworkConfiguration getConfig();

    /**
     * Returns a reference to the underlying event broker. You can use the reference to emit or subscribe to events of
     * the simulation.
     * 
     * @return
     */
    public EventBroker getEventBroker();

    /**
     * Creates a new node and instantiates the configured event stack.
     * 
     * @param nodeConfig
     *            The configuration containing information how to create and configure the node.
     * @return A reference to the newly created node.
     */
    public NodeRef createNode(NodeConfiguration nodeConfig);

    /**
     * Spawns a new application instance on the referenced node but doesn't start its main thread.
     * 
     * @param node
     * @param appDesc
     * @return
     */
    public ApplicationRef loadApplication(NodeRef node, ApplicationBundleDescriptor appDesc);

    /**
     * Shuts the simulation asynchronously down and returns the corresponding future to monitor progress.
     * 
     * @return
     */
    public Future<?> shutdown();

    /**
     * Returns the future for the end of the simulation.
     * 
     * @return
     */
    public Future<?> getSimulationEndFuture();
}
