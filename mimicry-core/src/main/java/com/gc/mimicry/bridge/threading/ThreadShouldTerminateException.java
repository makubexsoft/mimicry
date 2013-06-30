package com.gc.mimicry.bridge.threading;

import com.gc.mimicry.core.timing.Clock;

/**
 * This exception is used to terminate {@link ManagedThread}s. The exception is raised either by the {@link Clock} when
 * the thread is waiting/sleeping or by the {@link ThreadTerminationStratetgy} invoked each loop iteration.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ThreadShouldTerminateException extends RuntimeException
{

    private static final long serialVersionUID = 8789143078328218443L;

    public ThreadShouldTerminateException()
    {
    }
}
