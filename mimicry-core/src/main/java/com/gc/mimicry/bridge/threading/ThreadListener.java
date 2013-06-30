package com.gc.mimicry.bridge.threading;

/**
 * Implement this interface to get notified when all threads managed by the {@link ThreadManager} have been terminated.
 * 
 * @author Marc-Christian Schulze
 * @see ThreadManager
 * 
 */
public interface ThreadListener
{
    public void allThreadsTerminated(ThreadManager mgr);
}
