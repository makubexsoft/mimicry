package com.gc.mimicry.engine.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import com.gc.mimicry.util.concurrent.Future;

public interface RemoteApplication extends Remote
{
    public void start(String... commandArgs) throws RemoteException;

    public UUID getId() throws RemoteException;

    public Future<?> stop() throws RemoteException;

    public Future<?> getTerminationFuture() throws RemoteException;
}
