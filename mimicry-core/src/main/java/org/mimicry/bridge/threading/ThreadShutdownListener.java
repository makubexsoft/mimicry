package org.mimicry.bridge.threading;

/**
 * Implement this interface to get notified when a certain {@link IManagedThread} has been terminated.
 * 
 * @author Marc-Christian Schulze
 * @see IManagedThread
 */
public interface ThreadShutdownListener
{
    /**
     * The given thread recently has been terminated.
     * 
     * @param thread
     *            The thread that has been terminated.
     */
    public void threadShouldTerminate(IManagedThread thread);
}
