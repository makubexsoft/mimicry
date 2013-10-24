package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.Node;
import com.google.common.base.Preconditions;

public class ExportedNode extends UnicastRemoteObject implements RemoteNode
{
    private static final long serialVersionUID = -2939974562527811477L;
    private final Node delegate;

    protected ExportedNode(Node delegate) throws RemoteException
    {
        super();
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public ExportedApplication installApplication(String bundleName, String path) throws RemoteException
    {
        return new ExportedApplication(delegate.installApplication(bundleName, path));
    }

    @Override
    public UUID getId() throws RemoteException
    {
        return delegate.getId();
    }

    @Override
    public String getName() throws RemoteException
    {
        return delegate.getName();
    }

    @Override
    public EngineInfo getEngineInfo() throws RemoteException
    {
        return delegate.getEngineInfo();
    }
}
