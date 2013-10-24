package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.engine.Node;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.Session;
import com.google.common.base.Preconditions;

public class ImportedSession implements Session
{
    private final RemoteSession remote;
    private final CEPEngine siddhi;

    public ImportedSession(RemoteSession remote, CEPEngine siddhi)
    {
        Preconditions.checkNotNull(remote);
        Preconditions.checkNotNull(siddhi);
        this.remote = remote;
        this.siddhi = siddhi;
    }

    @Override
    public Node createNode(NodeParameters params)
    {
        try
        {
            return new ImportedNode(remote.createNode(params), siddhi);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to create node on remote session.", e);
        }
    }

    @Override
    public void close()
    {
        try
        {
            remote.close();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to close remote session.", e);
        }
    }
}
