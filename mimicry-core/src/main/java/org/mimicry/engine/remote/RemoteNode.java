package org.mimicry.engine.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import org.mimicry.engine.EngineInfo;


public interface RemoteNode extends Remote
{
    public RemoteApplication installApplication(String bundleName, String path) throws RemoteException;

    public UUID getId() throws RemoteException;

    public String getName() throws RemoteException;

    /**
     * Returns information on the engine that hosts this node.
     * 
     * @return
     */
    public EngineInfo getEngineInfo() throws RemoteException;
}
