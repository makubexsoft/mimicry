package com.gc.mimicry.util.concurrent;

/**
 * An obseravable future that results on completion in a value.
 * 
 * @author Marc-Christian Schulze
 * 
 * @param <T>
 *            The type of value.
 */
public interface ValueFuture<T> extends Future<ValueFuture<T>>
{

    /**
     * Returns the value of the computation. This method blocks uninterruptibly until the operation is finished.
     * 
     * @return
     */
    public T getValue();
}