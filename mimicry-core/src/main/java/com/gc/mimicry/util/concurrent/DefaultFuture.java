package com.gc.mimicry.util.concurrent;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of a {@link Future}.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class DefaultFuture extends AbstractFuture<DefaultFuture> implements Future<DefaultFuture>
{

    private final CopyOnWriteArrayList<FutureListener<? super DefaultFuture>> listener;

    public DefaultFuture()
    {
        listener = new CopyOnWriteArrayList<FutureListener<? super DefaultFuture>>();
    }

    @Override
    public void addFutureListener(final FutureListener<? super DefaultFuture> l)
    {
        doSynchronized(new Runnable()
        {

            @Override
            public void run()
            {
                listener.add(l);
                if (isDone())
                {
                    l.operationComplete(DefaultFuture.this);
                }
            }
        });
    }

    @Override
    public void removeFutureListener(FutureListener<? super DefaultFuture> l)
    {
        listener.remove(l);
    }

    @Override
    protected void notifyListener()
    {
        for (FutureListener<? super DefaultFuture> l : listener)
        {
            l.operationComplete(this);
        }
    }

    /**
     * Override to perform your custom cancellation.
     * 
     * @return whether the cancellation was successful or not.
     */
    @Override
    protected boolean performCancellation()
    {
        return false;
    }
}
