package org.mimicry.remote;

import java.rmi.RemoteException;

import org.mimicry.Node;
import org.mimicry.NodeParameters;
import org.mimicry.Session;
import org.mimicry.cep.CEPEngine;

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
