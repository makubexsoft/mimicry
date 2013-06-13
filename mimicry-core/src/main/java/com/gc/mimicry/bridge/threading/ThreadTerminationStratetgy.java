package com.gc.mimicry.bridge.threading;

import com.gc.mimicry.bridge.LoopInterceptionStrategy;

/**
 * This strategy throws a {@link ThreadShouldTerminateException} if the
 * {@link ManagedThread#isShuttingDown()} flag is set in order to trigger the
 * shutdown procedure.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ThreadTerminationStratetgy implements LoopInterceptionStrategy
{
	@Override
	public void intercept()
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			IManagedThread managedThread = (IManagedThread) thread;
			if ( managedThread.isShuttingDown() )
			{
				throw new ThreadShouldTerminateException();
			}
		}
	}
}
