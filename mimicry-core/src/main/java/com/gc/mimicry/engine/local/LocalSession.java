package com.gc.mimicry.engine.local;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.Session;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.engine.nodes.events.NodeCreatedEvent;
import com.google.common.base.Preconditions;

public class LocalSession implements Session
{
    private final Map<UUID, LocalNode> nodes;
    private final NodeFactory nodeFactory;
    private final File sessionDir;
    private final EventEngine engine;
    private final EventFactory eventFactory;

    public LocalSession(NodeFactory nodeFactory, File sessionDir, EventEngine engine, EventFactory factory)
    {
        Preconditions.checkNotNull(nodeFactory);
        Preconditions.checkNotNull(sessionDir);
        Preconditions.checkNotNull(engine);
        Preconditions.checkNotNull(factory);

        this.nodeFactory = nodeFactory;
        this.sessionDir = sessionDir;
        this.engine = engine;
        this.eventFactory = factory;

        nodes = new HashMap<UUID, LocalNode>();
    }

    public File getWorkspace()
    {
        return sessionDir;
    }

    @Override
    public void close()
    {
        for (LocalNode node : nodes.values())
        {
            node.close();
        }
        nodes.clear();
    }

    @Override
    public LocalNode createNode(NodeParameters params)
    {
        LocalNode node = nodeFactory.createNode(params, sessionDir);
        nodes.put(node.getId(), node);
        emitCreationEvent(node);
        return node;
    }

    private void emitCreationEvent(LocalNode node)
    {
        NodeCreatedEvent event = eventFactory.createEvent(NodeCreatedEvent.class);
        event.setNodeId(node.getId());
        engine.fireEvent(event);
    }
}
