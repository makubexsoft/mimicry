package org.mimicry.util.concurrent;

/**
 * A listener that can be attached to {@link Future}s and {@link ValueFuture}s to get notified when they complete.
 * 
 * @author Marc-Christian Schulze
 * 
 * @param <T>
 *            The concrete type of future to attach to.
 */
public interface FutureListener<T>
{

    public void operationComplete(T future);
}
