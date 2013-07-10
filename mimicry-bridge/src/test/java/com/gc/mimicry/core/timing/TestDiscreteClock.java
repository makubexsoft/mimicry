package com.gc.mimicry.core.timing;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.bridge.threading.ThreadShouldTerminateException;

public class TestDiscreteClock
{
	private static final long	TEN_SECONDS	= 10000;
	private DiscreteClock		clock;

	@Before
	public void setUp()
	{
		clock = new DiscreteClock( 0 );
	}

	private void assertMillis( long millis )
	{
		assertEquals( millis, clock.currentMillis() );
	}

	@Test
	public void testFrozenTime() throws InterruptedException
	{
		assertMillis( 0 );
		Thread.sleep( 100 );
		assertMillis( 0 );
	}

	@Test
	public void testSampling()
	{
		assertMillis( 0 );
		clock.advance( 200 );
		assertMillis( 200 );
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPreventSamplingInThePast()
	{
		assertMillis( 0 );
		clock.advance( -100 );
	}

	@Test
	public void testSleepTimeout() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					clock.sleep( 100 );
					counter.incrementAndGet();
				}
				catch ( InterruptedException e )
				{
				}
			}
		} );
		thread.start();

		Thread.sleep( 200 );

		assertEquals( 0, counter.get() );

		clock.advance( 150 );

		Thread.sleep( 50 );

		assertEquals( 1, counter.get() );
	}

	@Test
	public void testSleepTimeoutWithException() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					clock.sleep( 100 );
				}
				catch ( InterruptedException e )
				{
				}
				catch ( ThreadShouldTerminateException e )
				{
					counter.incrementAndGet();
				}
			}
		} );
		thread.start();

		Thread.sleep( 200 );

		assertEquals( 0, counter.get() );

		thread.shutdownGracefully();

		Thread.sleep( 200 );

		assertEquals( 1, counter.get() );
	}

	@Test
	public void testWaitTimeoutWithoutNotify() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();
		final Object lock = new Object();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					synchronized ( lock )
					{
						clock.waitOn( lock, 100 );
						counter.incrementAndGet();
					}
				}
				catch ( InterruptedException e )
				{
				}
			}
		} );
		thread.start();

		Thread.sleep( 200 );

		assertEquals( 0, counter.get() );

		clock.advance( 150 );

		Thread.sleep( 50 );

		assertEquals( 1, counter.get() );
	}

	@Test
	public void testWaitTimeoutWithNotify() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();
		final Object lock = new Object();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					synchronized ( lock )
					{
						clock.waitOn( lock, 100 );
						counter.incrementAndGet();
					}
				}
				catch ( InterruptedException e )
				{
				}
			}
		} );
		thread.start();

		Thread.sleep( 200 );

		assertEquals( 0, counter.get() );

		ManagedThread thread2 = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				synchronized ( lock )
				{
					clock.notifyOnTarget( lock );
				}
			}
		} );
		thread2.start();
		thread2.join();

		Thread.sleep( 50 );

		assertEquals( 1, counter.get() );
	}

	@Test
	public void testWaitInfiniteWithNotify() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();
		final Object lock = new Object();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					synchronized ( lock )
					{
						clock.waitOn( lock );
						counter.incrementAndGet();
					}
				}
				catch ( InterruptedException e )
				{
				}
			}
		} );
		thread.start();

		assertEquals( 0, counter.get() );

		ManagedThread thread2 = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				synchronized ( lock )
				{
					clock.notifyOnTarget( lock );
				}
			}
		} );
		thread2.start();
		thread2.join();

		Thread.sleep( 50 );

		assertEquals( 1, counter.get() );
	}

	@Test(timeout = TEN_SECONDS)
	public void testWaitTimeoutWithException() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();
		final Object lock = new Object();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					synchronized ( lock )
					{
						clock.waitOn( lock, 100 );
					}
				}
				catch ( InterruptedException e )
				{
				}
				catch ( ThreadShouldTerminateException e )
				{
					counter.incrementAndGet();
				}
			}
		} );
		thread.start();

		assertEquals( 0, counter.get() );

		thread.shutdownGracefully();

		thread.join();

		assertEquals( 1, counter.get() );
	}

	@Test(timeout = TEN_SECONDS)
	public void testWaitInfiniteWithException() throws InterruptedException
	{

		final AtomicInteger counter = new AtomicInteger();
		final Object lock = new Object();

		ManagedThread thread = new ManagedThread( new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					synchronized ( lock )
					{
						clock.waitOn( lock );
					}
				}
				catch ( InterruptedException e )
				{
				}
				catch ( ThreadShouldTerminateException e )
				{
					counter.incrementAndGet();
				}
			}
		} );
		thread.start();

		assertEquals( 0, counter.get() );

		Thread.sleep( 50 );

		thread.shutdownGracefully();
		thread.join();

		assertEquals( 1, counter.get() );
	}
}
