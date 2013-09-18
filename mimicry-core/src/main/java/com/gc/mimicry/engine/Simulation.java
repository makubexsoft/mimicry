package com.gc.mimicry.engine;

import java.util.Set;

import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * Basic interface for controlling a simulated network.
 * 
 * @author Marc-Christian Schulze
 */
public class Simulation
{
    private final Set<Session> sessions;
    private final NodeDistributionStrategy distStrategy;

    public Simulation(Set<Session> sessions, NodeDistributionStrategy distStrategy)
    {
        Preconditions.checkNotNull(sessions);
        Preconditions.checkNotNull(distStrategy);
        this.sessions = sessions;
        this.distStrategy = distStrategy;
    }

    public Node createNode(NodeParameters params)
    {
        return distStrategy.createNode(sessions, params);
    }

    public SimulationParameters getParameters()
    {
        return null;
    }

    /**
     * Returns a reference to the underlying event broker. You can use the reference to emit or subscribe to events of
     * the simulation.
     * 
     * @return
     */
    public EventEngine getEventEngine()
    {
        return null;
    }

    /**
     * Shuts the simulation asynchronously down and returns the corresponding future to monitor progress.
     * 
     * @return
     */
    public Future<?> shutdown()
    {
        return null;
    }

    /**
     * Returns the future for the end of the simulation.
     * 
     * @return
     */
    public Future<?> getSimulationEndFuture()
    {
        return null;
    }
}
