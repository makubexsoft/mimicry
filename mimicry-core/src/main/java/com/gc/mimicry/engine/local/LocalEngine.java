package com.gc.mimicry.engine.local;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.engine.Engine;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.SessionInfo;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.event.DefaultEventFactory;
import com.gc.mimicry.engine.event.Identity;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.engine.timing.TimelineFactory;
import com.gc.mimicry.util.IOUtils;
import com.google.common.base.Preconditions;

public class LocalEngine implements Engine
{
    private final Map<UUID, LocalSession> sessions;
    private final EventEngine broker;
    private final ApplicationRepository appRepo;
    private final File workspace;

    public LocalEngine(EventEngine broker, ApplicationRepository appRepo, File workspace)
    {
        Preconditions.checkNotNull(broker);
        Preconditions.checkNotNull(appRepo);
        Preconditions.checkNotNull(workspace);

        this.broker = broker;
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
    public LocalSession createSession(UUID sessionId, SimulationParameters params)
    {
        File sessionDir = new File(workspace, sessionId.toString());
        sessionDir.mkdirs();

        Timeline timeline = TimelineFactory.getDefault().createTimeline(params.getTimelineType(),
                params.getInitialTimeMillis());
        // TODO: select a more appropriate class loader
        ClassLoader ehLoader = ClassLoader.getSystemClassLoader();

        NodeFactory nodeFactory = new NodeFactory(broker, timeline, ehLoader, appRepo);

        LocalSession session = new LocalSession(nodeFactory, sessionDir, broker, DefaultEventFactory.create(Identity
                .create("Session " + sessionId)));
        sessions.put(sessionId, session);
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
