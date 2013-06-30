package com.gc.mimicry.util.concurrent;

import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultValueFuture<T> extends AbstractFuture<ValueFuture<T>> implements ValueFuture<T>
{

    private final CopyOnWriteArrayList<FutureListener<? super ValueFuture<T>>> listener;
    private T value;

    public DefaultValueFuture()
    {
        listener = new CopyOnWriteArrayList<FutureListener<? super ValueFuture<T>>>();
    }

    public DefaultValueFuture(T value)
    {
        listener = new CopyOnWriteArrayList<FutureListener<? super ValueFuture<T>>>();
        this.value = value;
        setSuccess();
    }

    public void addFutureListener(final FutureListener<? super ValueFuture<T>> l)
    {
        doSynchronized(new Runnable()
        {

            public void run()
            {
                listener.add(l);
                if (isDone())
                {
                    l.operationComplete(DefaultValueFuture.this);
                }
            }
        });
    }

    public void removeFutureListener(FutureListener<? super ValueFuture<T>> l)
    {
        listener.remove(l);
    }

    @Override
    protected void notifyListener()
    {
        for (FutureListener<? super ValueFuture<T>> l : listener)
        {
            l.operationComplete(this);
        }
    }

    public T getValue()
    {
        awaitUninterruptibly(Long.MAX_VALUE);
        return value;
    }

    /**
     * Sets the value of the future and invokes {@link #setSuccess()}.
     * 
     * @param value
     */
    public void setValue(T value)
    {
        this.value = value;
        setSuccess();
    }

    @Override
    protected boolean performCancellation()
    {
        return false;
    }
}
