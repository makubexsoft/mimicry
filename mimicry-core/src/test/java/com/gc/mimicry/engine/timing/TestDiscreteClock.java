package com.gc.mimicry.engine.timing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

public class TestDiscreteClock
{
    private DiscreteClock clock;

    @Before
    public void setUp()
    {
        clock = new DiscreteClock(0);
    }

    @Test
    public void testTimeDoesNotAdvanceAutomatically() throws InterruptedException
    {
        assertEquals(0, clock.currentMillis());
        Thread.sleep(100);
        assertEquals(0, clock.currentMillis());
    }

    @Test
    public void testTimeAdvancesCorrectly() throws InterruptedException
    {
        assertEquals(0, clock.currentMillis());
        clock.advance(100);
        assertEquals(100, clock.currentMillis());
    }

    @Test
    public void testClockDoesNotWaitForSystemTime() throws InterruptedException
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final Object obj = new Object();
        final long start = System.currentTimeMillis();
        Thread thread = runAsync(new Runnable()
        {

            @Override
            public void run()
            {
                synchronized (obj)
                {
                    try
                    {
                        latch.countDown();
                        clock.waitOnFor(obj, 5000);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
                }
            }
        });
        latch.await();
        synchronized (obj)
        {
            clock.advance(5000);
        }
        thread.join(10000);
        assertTrue((System.currentTimeMillis() - start) <= 200);
    }

    private Thread runAsync(Runnable job)
    {
        Thread thread = new Thread(job);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
