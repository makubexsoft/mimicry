package com.gc.mimicry.util.concurrent;

public interface ValueFuture<T> extends Future<ValueFuture<T>>
{

    /**
     * Returns the value of the computation. This method blocks uninterruptibly until the operation is finished.
     * 
     * @return
     */
    public T getValue();
}