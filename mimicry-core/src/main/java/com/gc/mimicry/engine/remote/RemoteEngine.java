package com.gc.mimicry.engine.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.SessionInfo;
import com.gc.mimicry.engine.SimulationParameters;

public interface RemoteEngine extends Remote
{
    public EngineInfo getEngineInfo() throws RemoteException;

    public RemoteSession createSession(UUID sessionId, SimulationParameters params) throws RemoteException;

    public List<SessionInfo> listSessions() throws RemoteException;

    public void destroySession(UUID sessionId) throws RemoteException;

    public RemoteSession findSession(UUID sessionId) throws RemoteException;

    public void deployApplication(String name, byte[] content) throws RemoteException;
}
