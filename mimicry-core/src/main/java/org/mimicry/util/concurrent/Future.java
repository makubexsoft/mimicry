package org.mimicry.util.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * An obseravable future that unlike the one of the JDK allows to register listener to it.
 * 
 * @author Marc-Christian Schulze
 * 
 * @param <T>
 *            The concrete subtype of {@link Future}.
 */
public interface Future<T extends Future<T>>
{

    /**
     * Returns true if and only if this future is complete, regardless of whether the operation was successful, failed,
     * or cancelled.
     * 
     * @return
     */
    public boolean isDone();

    /**
     * Returns true if and only if the I/O operation was completed successfully.
     * 
     * @return
     */
    public boolean isSuccess();

    /**
     * Returns true if and only if this future was cancelled by a cancel() method.
     * 
     * @return
     */
    public boolean isCancelled();

    /**
     * Returns the cause of the failed I/O operation if the I/O operation has failed.
     * 
     * @return
     */
    public Throwable getCause();

    /**
     * Adds the specified listener to this future. The specified listener is notified when this future is done. If this
     * future is already completed, the specified listener is notified immediately.
     * 
     * @param l
     */
    public void addFutureListener(FutureListener<? super T> l);

    /**
     * Removes the specified listener from this future. The specified listener is no longer notified when this future is
     * done. If the specified listener is not associated with this future, this method does nothing and returns
     * silently.
     * 
     * @param l
     */
    public void removeFutureListener(FutureListener<? super T> l);

    /**
     * Marks this future as a success and notifies all listeners.
     * 
     * @param object
     * @return
     */
    boolean setSuccess();

    /**
     * Marks this future as a failure and notifies all listeners.
     * 
     * @param cause
     * @return
     */
    boolean setFailure(Throwable cause);

    /**
     * Cancels the I/O operation associated with this future and notifies all listeners if canceled successfully.
     * 
     * @return
     */
    public boolean cancel();

    /**
     * Waits for this future to be completed within the specified time limit.
     * 
     * @param timeoutInMillis
     * @return
     * @throws InterruptedException
     */
    boolean await(long timeoutInMillis) throws InterruptedException;

    /**
     * Waits for this future to be completed within the specified time limit.
     * 
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Waits for this future to be completed within the specified time limit without interruption.
     * 
     * @param timeoutInMillis
     * @return
     */
    boolean awaitUninterruptibly(long timeoutInMillis);

    /**
     * Waits for this future to be completed within the specified time limit without interruption.
     * 
     * @param timeout
     * @param unit
     * @return
     */
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);
}
