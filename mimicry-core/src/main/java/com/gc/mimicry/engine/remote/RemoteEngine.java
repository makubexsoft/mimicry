package com.gc.mimicry.engine.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.SimulationParameters;

public interface RemoteEngine extends Remote
{
    public EngineInfo getEngineInfo() throws RemoteException;

    public RemoteSession createSession(UUID sessionId, SimulationParameters params) throws RemoteException;

    public Set<UUID> listSessions() throws RemoteException;

    public void destroySession(UUID sessionId) throws RemoteException;

    public RemoteSession findSession(UUID sessionId) throws RemoteException;

    public void deployApplication(String name, byte[] content) throws RemoteException;
}
