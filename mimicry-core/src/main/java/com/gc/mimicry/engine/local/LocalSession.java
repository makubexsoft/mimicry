package com.gc.mimicry.engine.local;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.Session;
import com.google.common.base.Preconditions;

public class LocalSession implements Session
{
    private final Map<UUID, LocalNode> nodes;
    private final NodeFactory nodeFactory;
    private final File sessionDir;

    public LocalSession(NodeFactory nodeFactory, File sessionDir)
    {
        Preconditions.checkNotNull(nodeFactory);
        Preconditions.checkNotNull(sessionDir);

        this.nodeFactory = nodeFactory;
        this.sessionDir = sessionDir;

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
        return node;
    }
}
