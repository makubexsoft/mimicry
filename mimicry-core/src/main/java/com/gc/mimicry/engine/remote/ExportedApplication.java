package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import com.gc.mimicry.engine.Application;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

public class ExportedApplication extends UnicastRemoteObject implements RemoteApplication
{
    private static final long serialVersionUID = 1140728509720371447L;
    private final Application delegate;

    protected ExportedApplication(Application delegate) throws RemoteException
    {
        super();
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public void start(String... commandArgs) throws RemoteException
    {
        delegate.start(commandArgs);
    }

    @Override
    public UUID getId() throws RemoteException
    {
        return delegate.getId();
    }

    @Override
    public Future<?> stop() throws RemoteException
    {
        return null;
    }

    @Override
    public Future<?> getTerminationFuture() throws RemoteException
    {
        return null;
    }
}
