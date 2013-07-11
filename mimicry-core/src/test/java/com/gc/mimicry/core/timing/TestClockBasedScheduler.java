package com.gc.mimicry.core.timing;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestClockBasedScheduler
{

    private DiscreteClock clock;
    private ClockBasedScheduler scheduler;

    @Before
    public void setUp()
    {
        clock = new DiscreteClock(0);
        scheduler = new ClockBasedScheduler(clock);
    }

    @Test
    public void testExecutionWaitsForClock() throws InterruptedException
    {
        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, 100, TimeUnit.MILLISECONDS);

        Thread.sleep(200);

        Mockito.verifyZeroInteractions(job);

        clock.advance(100);

        Thread.sleep(200);

        Mockito.verify(job).run();
    }

    @Test
    public void testJobsAreScheduledInCorrectOrder() throws InterruptedException
    {
        Runnable firstJob = Mockito.mock(Runnable.class);
        Runnable secondJob = Mockito.mock(Runnable.class);
        scheduler.schedule(firstJob, 100, TimeUnit.MILLISECONDS);
        scheduler.schedule(secondJob, 500, TimeUnit.MILLISECONDS);

        clock.advance(200);

        Thread.sleep(200);

        Mockito.verify(firstJob).run();
        Mockito.verifyZeroInteractions(secondJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNonNegativeTimeouts() throws InterruptedException
    {
        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, -100, TimeUnit.MILLISECONDS);
    }
}
