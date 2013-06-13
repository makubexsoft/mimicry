package com.gc.mimicry.bridge.threading;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * This class is used as registry for all managed threads created.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ThreadManager
{
	/**
	 * Creates an empty thread manager without any attached
	 * {@link IManagedThread}s.
	 * 
	 * @param appId
	 */
	public ThreadManager(UUID appId)
	{
		Preconditions.checkNotNull( appId );
		this.appId = appId;

		threads = new CopyOnWriteArrayList<IManagedThread>();
		shutdownFuture = new DefaultFuture();
	}

	public void addThread( IManagedThread thread )
	{
		threads.add( thread );
	}

	public void threadTerminated( IManagedThread thread )
	{
		threads.remove( thread );
		if ( threads.size() == 0 )
		{
			shutdownFuture.setSuccess();
		}
	}

	public void threadTerminated( IManagedThread thread, Throwable th )
	{
		// TODO: we can evaluate the exception
		threads.remove( thread );
		if ( threads.size() == 0 )
		{
			shutdownFuture.setSuccess();
		}
	}

	public UUID getApplicationId()
	{
		return appId;
	}

	/**
	 * Returns the shutdown future that is triggered once all threads added to
	 * this instance have been terminated.
	 * 
	 * @return
	 */
	public Future<?> getShutdownFuture()
	{
		return shutdownFuture;
	}

	/**
	 * Triggers the shutdown procedure of all attached threads and returns the
	 * shutdown future that is activated once all threads have been terminated.
	 * 
	 * @return
	 */
	public Future<?> shutdownAllThreads()
	{
		for ( IManagedThread thread : threads )
		{
			if ( thread != null )
			{
				thread.shutdownGracefully();
			}
		}
		return shutdownFuture;
	}

	private final CopyOnWriteArrayList<IManagedThread>	threads;
	private final UUID									appId;
	private final Future<?>								shutdownFuture;
}
