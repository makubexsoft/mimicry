package org.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.mimicry.engine.NodeParameters;
import org.mimicry.engine.Session;

import com.google.common.base.Preconditions;

public class ExportedSession extends UnicastRemoteObject implements RemoteSession
{
    private static final long serialVersionUID = -138633450090296330L;
    private final Session delegate;

    public ExportedSession(Session delegate) throws RemoteException
    {
        super();
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public RemoteNode createNode(NodeParameters params) throws RemoteException
    {
        return new ExportedNode(delegate.createNode(params));
    }

    @Override
    public void close() throws RemoteException
    {
        delegate.close();
    }
}
