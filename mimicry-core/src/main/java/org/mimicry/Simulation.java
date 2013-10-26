package org.mimicry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mimicry.cep.CEPEngine;
import org.mimicry.util.concurrent.Future;

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
    private final CEPEngine eventEngine;
    private final SimulationParameters params;
    private final List<Node> nodes;

    private Simulation(Set<Session> sessions, NodeDistributionStrategy distStrategy, CEPEngine eventEngine,
            SimulationParameters params)
    {
        Preconditions.checkNotNull(sessions);
        Preconditions.checkNotNull(distStrategy);
        Preconditions.checkNotNull(eventEngine);
        Preconditions.checkNotNull(params);

        this.sessions = sessions;
        this.distStrategy = distStrategy;
        this.eventEngine = eventEngine;
        this.params = params;

        nodes = new ArrayList<Node>();
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    public Node getNodeByName(String name)
    {
        for (Node node : nodes)
        {
            if (node.getName().equals(name))
            {
                return node;
            }
        }
        return null;
    }

    public Node getNodeById(UUID id)
    {
        for (Node node : nodes)
        {
            if (node.getId().equals(id))
            {
                return node;
            }
        }
        return null;
    }

    public Node createNode(NodeParameters params)
    {
        Node node = distStrategy.createNode(sessions, params);
        nodes.add(node);
        return node;
    }

    public SimulationParameters getParameters()
    {
        return params;
    }

    /**
     * Returns a reference to the underlying event broker. You can use the reference to emit or subscribe to events of
     * the simulation.
     * 
     * @return
     */
    public CEPEngine getEventEngine()
    {
        return eventEngine;
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

    public static class Builder
    {
        private NodeDistributionStrategy strategy;
        private final Set<Session> sessions = new HashSet<Session>();
        private CEPEngine eventEngine;
        private SimulationParameters params;

        public Builder withNodeDistributionStrategy(NodeDistributionStrategy strategy)
        {
            this.strategy = strategy;
            return this;
        }

        public Builder withSimulationParameters(SimulationParameters params)
        {
            this.params = params;
            return this;
        }

        public Builder withEventEngine(CEPEngine eventEngine)
        {
            this.eventEngine = eventEngine;
            return this;
        }

        public Builder addSession(Session session)
        {
            sessions.add(session);
            return this;
        }

        public Simulation build()
        {
            return new Simulation(sessions, strategy, eventEngine, params);
        }
    }
}
