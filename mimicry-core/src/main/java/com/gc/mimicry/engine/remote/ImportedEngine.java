package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.CEPEngineFactory;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngine;
import com.gc.mimicry.engine.Engine;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.Session;
import com.gc.mimicry.engine.SimulationParameters;
import com.google.common.base.Preconditions;

public class ImportedEngine implements Engine
{
    private final RemoteEngine remote;
    private final Map<UUID, Session> sessions;
    private final CEPEngineFactory engineFactory;

    public ImportedEngine(RemoteEngine remote, CEPEngineFactory engineFactory)
    {
        Preconditions.checkNotNull(remote);
        Preconditions.checkNotNull(engineFactory);
        this.remote = remote;
        this.engineFactory = engineFactory;

        sessions = new HashMap<UUID, Session>();
    }

    @Override
    public EngineInfo getEngineInfo()
    {
        try
        {
            return remote.getEngineInfo();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to obtain engine info of remote engine.", e);
        }
    }

    @Override
    public Session createSession(UUID sessionId, SimulationParameters params)
    {
        CEPEngine eventEngine = engineFactory.create(sessionId.toString());
        try
        {
            ImportedSession session = new ImportedSession(remote.createSession(sessionId, params), eventEngine);
            sessions.put(sessionId, session);
            return session;
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to create session on remote engine.", e);
        }
    }

    @Override
    public Set<UUID> listSessions()
    {
        try
        {
            return remote.listSessions();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to list sessions of remote engine.", e);
        }
    }

    @Override
    public void destroySession(UUID sessionId)
    {
        try
        {
            remote.destroySession(sessionId);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to destroy session of remote node.", e);
        }
    }

    @Override
    public Session findSession(UUID sessionId)
    {
        Session session = sessions.get(sessionId);
        if (session != null)
        {
            return session;
        }

        CEPEngine eventEngine = new SiddhiCEPEngine(sessionId.toString());
        try
        {
            return new ImportedSession(remote.findSession(sessionId), eventEngine);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to find session on remote engine.", e);
        }
    }

    @Override
    public void deployApplication(String name, byte[] content)
    {
        try
        {
            remote.deployApplication(name, content);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to deploy application on remote engine.");
        }
    }
}
