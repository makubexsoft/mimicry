package com.gc.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.util.UUID;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.Node;
import com.google.common.base.Preconditions;

public class ImportedNode implements Node
{
    private final RemoteNode remote;
    private final CEPEngine siddhi;

    public ImportedNode(RemoteNode remote, CEPEngine siddhi)
    {
        Preconditions.checkNotNull(remote);
        Preconditions.checkNotNull(siddhi);
        this.remote = remote;
        this.siddhi = siddhi;
    }

    @Override
    public Application installApplication(String bundleName, String path)
    {
        try
        {
            return new ImportedApplication(remote.installApplication(bundleName, path), siddhi);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to install application on remote node.", e);
        }
    }

    @Override
    public UUID getId()
    {
        try
        {
            return remote.getId();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to obtain id of remote node.");
        }
    }

    @Override
    public String getName()
    {
        try
        {
            return remote.getName();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to obtain name of remote node.");
        }
    }

    @Override
    public EngineInfo getEngineInfo()
    {
        try
        {
            return remote.getEngineInfo();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to obtain engine info of remote node.");
        }
    }
}
