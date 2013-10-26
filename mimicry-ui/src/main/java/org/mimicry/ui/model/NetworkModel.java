package org.mimicry.ui.model;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.mimicry.engine.EventListener;
import org.mimicry.engine.Node;
import org.mimicry.engine.Simulation;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.nodes.events.ApplicationInstalledEvent;
import org.mimicry.engine.nodes.events.ApplicationStartedEvent;
import org.mimicry.engine.nodes.events.NodeCreatedEvent;
import org.mimicry.engine.nodes.events.NodeRemovedEvent;

import com.google.common.base.Preconditions;

public class NetworkModel implements Closeable, EventListener
{
    private Simulation simulation;
    private List<Node> nodes;

    public NetworkModel(Simulation simulation)
    {
        Preconditions.checkNotNull(simulation);

        this.simulation = simulation;

        nodes = new ArrayList<Node>();
        simulation.getEventEngine().addEventListener(this);
    }

    @Override
    public void close()
    {
        simulation.getEventEngine().removeEventListener(this);
    }

    @Override
    public void handleEvent(ApplicationEvent evt)
    {
        if (evt instanceof NodeCreatedEvent)
        {
            NodeCreatedEvent nodeCreatedEvent = (NodeCreatedEvent) evt;
            nodes.add(simulation.getNodeById(nodeCreatedEvent.getNodeId()));
        }
        else if (evt instanceof NodeRemovedEvent)
        {

        }
        else if (evt instanceof ApplicationInstalledEvent)
        {

        }
        else if (evt instanceof ApplicationStartedEvent)
        {

        }
        // ...
    }
}
