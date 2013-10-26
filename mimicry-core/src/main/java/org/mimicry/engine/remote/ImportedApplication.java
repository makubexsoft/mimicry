package org.mimicry.engine.remote;

import java.rmi.RemoteException;
import java.util.UUID;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Event;
import org.mimicry.cep.Query;
import org.mimicry.cep.QueryListener;
import org.mimicry.engine.Application;
import org.mimicry.util.concurrent.DefaultFuture;
import org.mimicry.util.concurrent.Future;

import com.google.common.base.Preconditions;

public class ImportedApplication implements Application
{
    private final RemoteApplication remote;
    private final DefaultFuture terminationFuture;

    public ImportedApplication(RemoteApplication remote, final CEPEngine siddhi)
    {
        Preconditions.checkNotNull(remote);
        Preconditions.checkNotNull(siddhi);
        this.remote = remote;

        terminationFuture = new DefaultFuture();
        final Query query = siddhi.addQuery("from ???[applicationId == " + getId() + "]");
        query.addQueryListener(new QueryListener()
        {
            @Override
            public void receive(long timestamp, Event[] inEvents, Event[] outEvents)
            {
                terminationFuture.setSuccess();
                query.close();
            }
        });
    }

    @Override
    public void start(String... commandArgs)
    {
        try
        {
            remote.start(commandArgs);
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to start remote application.", e);
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
            throw new RuntimeException("Failed to obtain id of remote application.", e);
        }
    }

    @Override
    public Future<?> stop()
    {
        try
        {
            remote.stop();
        }
        catch (RemoteException e)
        {
            throw new RuntimeException("Failed to stop remote application.", e);
        }
        return terminationFuture;
    }

    @Override
    public Future<?> getTerminationFuture()
    {
        return terminationFuture;
    }
}
