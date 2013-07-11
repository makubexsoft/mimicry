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
    public void testExecutionWaitsForClockUsingSystemTime() throws InterruptedException
    {
        ClockBasedScheduler scheduler = new ClockBasedScheduler(new SystemClock());

        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, 500, TimeUnit.MILLISECONDS);

        Thread.sleep(100);

        Mockito.verifyZeroInteractions(job);
    }

    @Test
    public void testJobsAreScheduledInCorrectOrderUsingSystemTime() throws InterruptedException
    {
        ClockBasedScheduler scheduler = new ClockBasedScheduler(new SystemClock());

        Runnable firstJob = Mockito.mock(Runnable.class);
        Runnable secondJob = Mockito.mock(Runnable.class);
        scheduler.schedule(firstJob, 100, TimeUnit.MILLISECONDS);
        scheduler.schedule(secondJob, 500, TimeUnit.MILLISECONDS);

        Mockito.verify(firstJob, Mockito.timeout(300)).run();
        Mockito.verifyZeroInteractions(secondJob);
    }

    @Test
    public void testExecutionWaitsForClock() throws InterruptedException
    {
        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, 100, TimeUnit.MILLISECONDS);

        Thread.sleep(500);

        Mockito.verifyZeroInteractions(job);

        clock.advance(100);

        Mockito.verify(job, Mockito.timeout(200)).run();
    }

    @Test
    public void testExecutionDoesNotUseSystemClock() throws InterruptedException
    {
        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, 5, TimeUnit.SECONDS);

        Thread.sleep(500);
        Mockito.verifyZeroInteractions(job);

        clock.advance(5000);

        Mockito.verify(job, Mockito.timeout(500)).run();
    }

    @Test
    public void testJobsAreScheduledInCorrectOrder() throws InterruptedException
    {
        Runnable firstJob = Mockito.mock(Runnable.class);
        Runnable secondJob = Mockito.mock(Runnable.class);
        scheduler.schedule(firstJob, 100, TimeUnit.MILLISECONDS);
        scheduler.schedule(secondJob, 500, TimeUnit.MILLISECONDS);

        clock.advance(200);

        Mockito.verify(firstJob, Mockito.timeout(20000)).run();
        Mockito.verifyZeroInteractions(secondJob);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForNonNegativeTimeouts() throws InterruptedException
    {
        Runnable job = Mockito.mock(Runnable.class);
        scheduler.schedule(job, -100, TimeUnit.MILLISECONDS);
    }
}
