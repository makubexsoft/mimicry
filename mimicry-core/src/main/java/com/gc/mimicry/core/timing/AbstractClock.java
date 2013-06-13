package com.gc.mimicry.core.timing;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import com.gc.mimicry.bridge.threading.IManagedThread;
import com.gc.mimicry.bridge.threading.ThreadShouldTerminateException;
import com.gc.mimicry.bridge.threading.ThreadShutdownListener;

/**
 * Abstract base class that implements all threading related functionality.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public abstract class AbstractClock implements Clock, ThreadShutdownListener
{

	private static long									STATE_CHECKING_DELAY_IN_MILLIS	= 10;
	private final WeakHashMap<IManagedThread, Object>	blockedThreads;
	private final Set<Object>							notifications;

	public AbstractClock()
	{
		blockedThreads = new WeakHashMap<IManagedThread, Object>();
		notifications = Collections.synchronizedSet( new HashSet<Object>() );
	}

	@Override
	public void sleep( long periodMillis ) throws InterruptedException
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			IManagedThread managedThread = (IManagedThread) thread;
			synchronized ( blockedThreads )
			{
				managedThread.addThreadShutdownListener( this );
			}

			long startTime = currentMillis();
			while ( !timePassed( startTime, periodMillis ) )
			{
				try
				{
					Thread.sleep( STATE_CHECKING_DELAY_IN_MILLIS );
				}
				finally
				{
					if ( managedThread.isShuttingDown() )
					{
						throw new ThreadShouldTerminateException();
					}
				}
			}
		}
		else
		{
			Thread.sleep( periodMillis );
		}
	}

	private boolean timePassed( long startMillis, long periodMillis )
	{
		return currentMillis() > (startMillis + periodMillis);
	}

	@Override
	public void waitOn( Object target ) throws InterruptedException
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			IManagedThread managedThread = (IManagedThread) thread;
			synchronized ( blockedThreads )
			{
				managedThread.addThreadShutdownListener( this );
				blockedThreads.put( managedThread, target );
			}
		}
		try
		{
			target.wait();
		}
		finally
		{
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

	@Override
	public void waitOn( Object target, long periodMillis ) throws InterruptedException
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			IManagedThread managedThread = (IManagedThread) thread;
			synchronized ( blockedThreads )
			{
				managedThread.addThreadShutdownListener( this );
				blockedThreads.put( managedThread, target );
			}
			try
			{
				long startMillis = currentMillis();
				while ( !timePassed( startMillis, periodMillis ) )
				{
					try
					{
						target.wait( STATE_CHECKING_DELAY_IN_MILLIS );
						if ( notifications.contains( target ) )
						{
							notifications.remove( target );
							return;
						}
					}
					finally
					{
						if ( managedThread.isShuttingDown() )
						{
							throw new ThreadShouldTerminateException();
						}
					}
				}
			}
			finally
			{
				synchronized ( blockedThreads )
				{
					blockedThreads.remove( managedThread );
				}
			}
		}
		else
		{
			target.wait( periodMillis );
		}
	}

	@Override
	public void threadShouldTerminate( IManagedThread thread )
	{
		synchronized ( blockedThreads )
		{
			thread.removeThreadShutdownListener( this );
			Object object = blockedThreads.remove( thread );
			if ( object != null )
			{
				synchronized ( object )
				{
					object.notifyAll();
				}
			}
		}
	}

	@Override
	public void notifyOnTarget( Object target )
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			notifications.add( target );
			IManagedThread managedThread = (IManagedThread) thread;
			if ( managedThread.isShuttingDown() )
			{
				target.notifyAll();
			}
			else
			{
				target.notify();
			}
		}
		else
		{
			target.notify();
		}
	}

	@Override
	public void notifyAllOnTarget( Object target )
	{
		Thread thread = Thread.currentThread();
		if ( thread instanceof IManagedThread )
		{
			notifications.add( target );
		}
		target.notifyAll();
	}
}
