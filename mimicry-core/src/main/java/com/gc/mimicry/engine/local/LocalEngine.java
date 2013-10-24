package com.gc.mimicry.engine.local;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngine;
import com.gc.mimicry.engine.Engine;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.SessionInfo;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.engine.timing.TimelineFactory;
import com.gc.mimicry.ext.timing.ClockDriver;
import com.gc.mimicry.util.IOUtils;
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

    public LocalEngine(ApplicationRepository appRepo, File workspace)
    {
        Preconditions.checkNotNull(appRepo);
        Preconditions.checkNotNull(workspace);

        this.appRepo = appRepo;
        this.workspace = workspace;

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
        // TODO: select a more appropriate class loader
        ClassLoader ehLoader = ClassLoader.getSystemClassLoader();

        CEPEngine eventEngine = new SiddhiCEPEngine(simulationId.toString());

        Timeline timeline = factory.createTimeline(params.getTimelineType(), params.getInitialTimeMillis());

        NodeFactory nodeFactory = new NodeFactory(eventEngine, timeline, ehLoader, appRepo);
        LocalSession session = new LocalSession(nodeFactory, sessionDir, eventEngine);

        session.attachResource(new ClockDriver(eventEngine, timeline));

        sessions.put(simulationId, session);
        return session;
    }

    @Override
    public List<SessionInfo> listSessions()
    {
        // TODO Auto-generated method stub
        return new ArrayList<SessionInfo>();
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
