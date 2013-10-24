package com.gc.mimicry.engine.remote;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.gc.mimicry.engine.NodeParameters;

public interface RemoteSession extends Remote, Closeable
{
    public RemoteNode createNode(NodeParameters params) throws RemoteException;

    @Override
    public void close() throws RemoteException;
}
