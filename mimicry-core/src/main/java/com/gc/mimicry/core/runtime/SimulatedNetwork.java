package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockType;
import com.gc.mimicry.util.concurrent.Future;

/**
 * 
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface SimulatedNetwork
{
	/**
	 * Installs an implementation of the given {@link ClockType}. This needs to
	 * be done before you create any nodes.
	 * 
	 * @param type
	 *            The type of clock that shall be installed.
	 * @return A reference to the system's clock.
	 * @throws IllegalStateException
	 *             If you invoke it after nodes have been created. This
	 *             limitation might get removed in future versions.
	 */
	public Clock installClock( ClockType type );

	/**
	 * Creates a new node and instantiates the configured event stack.
	 * 
	 * @param nodeConfig
	 *            The configuration containing information how to create and
	 *            configure the node.
	 * @return A reference to the newly created node.
	 */
	public NodeRef spawnNode( NodeConfiguration nodeConfig );

	/**
	 * Spawns a new application instance on the referenced node but doesn't
	 * start it's main thread.
	 * 
	 * @param node
	 * @param appDesc
	 * @return
	 */
	public ApplicationRef spawnApplication( NodeRef node, ApplicationDescriptor appDesc );

	/**
	 * Starts the main thread of the referenced application.
	 * 
	 * @param app
	 *            Reference to the application to start.
	 */
	public void startApplication( ApplicationRef app );

	/**
	 * Shuts the simulation asynchronously down and returns the corresponding
	 * future to monitor progress.
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
