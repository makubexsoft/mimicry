package org.mimicry.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.UUID;

import org.mimicry.Engine;
import org.mimicry.EngineInfo;
import org.mimicry.SimulationParameters;

import com.google.common.base.Preconditions;

public class ExportedEngine extends UnicastRemoteObject implements RemoteEngine
{
    private static final long serialVersionUID = 2040162156320482192L;
    private final Engine delegate;

    public ExportedEngine(Engine delegate) throws RemoteException
    {
        super();
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public EngineInfo getEngineInfo() throws RemoteException
    {
        return delegate.getEngineInfo();
    }

    @Override
    public RemoteSession createSession(UUID sessionId, SimulationParameters params) throws RemoteException
    {
        return new ExportedSession(delegate.createSession(sessionId, params));
    }

    @Override
    public Set<UUID> listSessions() throws RemoteException
    {
        return delegate.listSessions();
    }

    @Override
    public void destroySession(UUID sessionId) throws RemoteException
    {
        delegate.destroySession(sessionId);
    }

    @Override
    public RemoteSession findSession(UUID sessionId) throws RemoteException
    {
        return new ExportedSession(delegate.findSession(sessionId));
    }

    @Override
    public void deployApplication(String name, byte[] content) throws RemoteException
    {
        delegate.deployApplication(name, content);
    }
}
