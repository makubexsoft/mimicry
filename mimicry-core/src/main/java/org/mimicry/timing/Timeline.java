package org.mimicry.timing;

/**
 * This interface models all timing related functionality and is used by the TimingAspect.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface Timeline
{

    /**
     * Returns the current time in milliseconds.
     * 
     * @return The current time in milliseconds.
     */
    public long currentMillis();

    /**
     * Causes the current thread to sleep for the given period.
     * 
     * @param millis
     *            The time to sleep in milliseconds.
     * @throws InterruptedException
     *             If the thread has been interrupted while sleeping.
     */
    public void sleepFor(long timeInMillis) throws InterruptedException;

    public void sleepUntil(long timeUntilInMillis) throws InterruptedException;

    /**
     * Waits on the monitor of the given target. Requires the calling thread to own the target object's monitor.
     * 
     * @param target
     * @throws InterruptedException
     */
    public void waitOn(Object target) throws InterruptedException;

    /**
     * Waits on the monitor of the given target for the specified amount of time. Requires the calling thread to own the
     * target object's monitor.
     * 
     * @param target
     * @param timeoutInMillis
     * @throws InterruptedException
     */
    public void waitOnFor(Object target, long timeoutInMillis) throws InterruptedException;

    public void waitOnUntil(Object target, long timeUntilInMillis) throws InterruptedException;

    /**
     * Notifies the given target. Requires the calling thread to own the target object's monitor.
     * 
     * @param target
     */
    public void notifyOnTarget(Object target);

    /**
     * Notifies all waiting threads in the given target. Requires the calling thread to own the target object's monitor.
     * 
     * @param target
     */
    public void notifyAllOnTarget(Object target);
}
