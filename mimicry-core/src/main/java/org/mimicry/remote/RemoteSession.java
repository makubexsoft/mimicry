package org.mimicry.remote;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.mimicry.NodeParameters;


public interface RemoteSession extends Remote, Closeable
{
    public RemoteNode createNode(NodeParameters params) throws RemoteException;

    @Override
    public void close() throws RemoteException;
}
