package org.mimicry.engine.local;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.CEPEngineFactory;
import org.mimicry.engine.Engine;
import org.mimicry.engine.EngineInfo;
import org.mimicry.engine.SimulationParameters;
import org.mimicry.engine.deployment.ApplicationRepository;
import org.mimicry.engine.timing.Timeline;
import org.mimicry.engine.timing.TimelineFactory;
import org.mimicry.ext.timing.ClockDriver;
import org.mimicry.util.IOUtils;

import com.google.common.base.Preconditions;

/**
 * An engine hosted within the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LocalEngine implements Engine
{
    private final Map<UUID, LocalSession> sessions;
    private final ApplicationRepository appRepo;
    private final File workspace;
    private final CEPEngineFactory engineFactory;

    public LocalEngine(ApplicationRepository appRepo, File workspace, CEPEngineFactory engineFactory)
    {
        Preconditions.checkNotNull(appRepo);
        Preconditions.checkNotNull(workspace);
        Preconditions.checkNotNull(engineFactory);

        this.appRepo = appRepo;
        this.workspace = workspace;
        this.engineFactory = engineFactory;

        sessions = new HashMap<UUID, LocalSession>();
    }

    @Override
    public EngineInfo getEngineInfo()
    {
        return EngineInfo.fromLocalJVM();
    }

    @Override
    public LocalSession createSession(UUID simulationId, SimulationParameters params)
    {
        File sessionDir = new File(workspace, simulationId.toString());
        sessionDir.mkdirs();

        TimelineFactory factory = TimelineFactory.getDefault();
        Timeline timeline = factory.createTimeline(params.getTimelineType(), params.getInitialTimeMillis());

        // TODO: select a more appropriate class loader
        ClassLoader ehLoader = ClassLoader.getSystemClassLoader();

        CEPEngine eventEngine = engineFactory.create(simulationId.toString());

        NodeFactory nodeFactory = new NodeFactory(eventEngine, timeline, ehLoader, appRepo);
        LocalSession session = new LocalSession(nodeFactory, sessionDir, eventEngine);

        session.attachResource(new ClockDriver(eventEngine, timeline));

        sessions.put(simulationId, session);
        return session;
    }

    @Override
    public Set<UUID> listSessions()
    {
        return new HashSet<UUID>(sessions.keySet());
    }

    @Override
    public void destroySession(UUID sessionId)
    {
        LocalSession session = sessions.remove(sessionId);
        if (session != null)
        {
            session.close();
            IOUtils.deleteRecursivly(session.getWorkspace());
        }
    }

    @Override
    public LocalSession findSession(UUID sessionId)
    {
        return sessions.get(sessionId);
    }

    @Override
    public void deployApplication(String name, byte[] content)
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(content);
        try
        {
            appRepo.storeBundle(name, stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeSilently(stream);
        }
    }
}
