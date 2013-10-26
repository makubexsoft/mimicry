package org.mimicry.engine.local;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.engine.EngineInfo;
import org.mimicry.engine.NodeParameters;
import org.mimicry.engine.Session;
import org.mimicry.engine.streams.NodeHasBeenCreatedStream;
import org.mimicry.util.BaseResourceManager;

import com.google.common.base.Preconditions;

/**
 * A simulation session running on a {@link LocalEngine} within the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LocalSession extends BaseResourceManager implements Session
{
    private final Map<UUID, LocalNode> nodes;
    private final NodeFactory nodeFactory;
    private final File sessionDir;
    private final Stream stream;
    private final CEPEngine eventEngine;

    public LocalSession(NodeFactory nodeFactory, File sessionDir, CEPEngine eventEngine)
    {
        Preconditions.checkNotNull(nodeFactory);
        Preconditions.checkNotNull(sessionDir);
        Preconditions.checkNotNull(eventEngine);

        this.nodeFactory = nodeFactory;
        this.sessionDir = sessionDir;
        this.eventEngine = eventEngine;

        stream = NodeHasBeenCreatedStream.get(eventEngine);
        nodes = new HashMap<UUID, LocalNode>();
    }

    public CEPEngine getEventEngine()
    {
        return eventEngine;
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
        EngineInfo engineInfo = EngineInfo.fromLocalJVM();
        stream.send(nodeFactory.getTimeline().currentMillis(), node.getId(), engineInfo.getOsVersion(),
                engineInfo.getArchitecture(), engineInfo.getJavaVersion(), engineInfo.getNumberCores(),
                engineInfo.getOperatingSystem());
    }
}
