package com.gc.mimicry.core.timing;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

/**
 * This scheduler executes all schedules job in a single daemon thread that is created per instance of this class.
 * 
 * @author Marc-Christian Schulze
 *
 */
public class ClockBasedScheduler implements Scheduler, Closeable
{

	private static final int			JOB_CHECKING_DELAY_MILLIS	= 100;
	private volatile boolean			shouldRun					= true;
	private Thread						thread;
	private Clock						clock;
	private final List<ScheduledJob>	jobs;

	public ClockBasedScheduler(Clock clock)
	{
		Preconditions.checkNotNull( clock );
		this.clock = clock;
		jobs = new CopyOnWriteArrayList<ScheduledJob>();
		thread = new Thread( new JobExecutor() );
		thread.setDaemon( true );
		thread.start();
	}

	public void schedule( Runnable job, long delay, TimeUnit unit )
	{
		synchronized ( jobs )
		{
			jobs.add( new ScheduledJob( clock.currentMillis() + unit.toMillis( delay ), job ) );
			jobs.notify();
		}
	}

	public void close()
	{
		shouldRun = false;
	}

	private static class ScheduledJob
	{
		public long		timeInMillis;
		public Runnable	runnable;

		public ScheduledJob(long timeInMillis, Runnable job)
		{
			this.timeInMillis = timeInMillis;
			this.runnable = job;
		}
	}

	private class JobExecutor implements Runnable
	{

		public void run()
		{
			while ( shouldRun )
			{
				Runnable job = takeNextJob();
				job.run();
			}
		}

		private Runnable takeNextJob()
		{
			synchronized ( jobs )
			{
				while ( shouldRun )
				{
					ScheduledJob nextJob = null;
					for ( ScheduledJob job : jobs )
					{
						if ( job.timeInMillis <= clock.currentMillis() )
						{
							nextJob = job;
							break;
						}
					}
					if ( nextJob != null )
					{
						jobs.remove( nextJob );
						return nextJob.runnable;
					}
					try
					{
						jobs.wait( JOB_CHECKING_DELAY_MILLIS );
					}
					catch ( InterruptedException e )
					{
						// suppress
					}
				}
				return new NoOperation();
			}
		}
	}

	private static class NoOperation implements Runnable
	{

		public void run()
		{
		}
	}
}
