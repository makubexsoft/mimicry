package com.gc.mimicry.core.timing;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

public class TestClockBasedScheduler
{

	private DiscreteClock		clock;
	private ClockBasedScheduler	scheduler;

	@Before
	public void setUp()
	{
		clock = new DiscreteClock( 0 );
		scheduler = new ClockBasedScheduler( clock );
	}

	@Test
	public void test() throws InterruptedException
	{
		final AtomicInteger counter = new AtomicInteger();
		scheduler.schedule( new Runnable()
		{

			public void run()
			{
				counter.incrementAndGet();
			}
		}, 100, TimeUnit.MILLISECONDS );

		Thread.sleep( 200 );

		assertEquals( 0, counter.get() );

		clock.sample( 100 );

		Thread.sleep( 200 );

		assertEquals( 1, counter.get() );
	}
}
