package org.mimicry.cep;

import java.util.concurrent.CopyOnWriteArrayList;

import org.mimicry.util.concurrent.AbstractFuture;
import org.mimicry.util.concurrent.Future;
import org.mimicry.util.concurrent.FutureListener;

public class EventFuture extends AbstractFuture<EventFuture> implements Future<EventFuture>
{
    private CopyOnWriteArrayList<FutureListener<? super EventFuture>> listener;
    {
        listener = new CopyOnWriteArrayList<FutureListener<? super EventFuture>>();
    }

    public EventFuture(Query query)
    {
        query.addQueryListener(new QueryListener()
        {

            @Override
            public void receive(long timestamp, Event[] inEvents, Event[] outEvents)
            {
                setSuccess();
            }
        });
    }

    public EventFuture(Stream stream)
    {
        stream.addStreamListener(new StreamListener()
        {

            @Override
            public void receive(Event[] events)
            {
                setSuccess();
            }
        });
    }

    @Override
    public void addFutureListener(FutureListener<? super EventFuture> l)
    {
        listener.add(l);
    }

    @Override
    public void removeFutureListener(FutureListener<? super EventFuture> l)
    {
        listener.remove(l);
    }

    @Override
    protected boolean performCancellation()
    {
        return false;
    }

    @Override
    protected void notifyListener()
    {
        for (FutureListener<? super EventFuture> l : listener)
        {
            l.operationComplete(this);
        }
    }
}
