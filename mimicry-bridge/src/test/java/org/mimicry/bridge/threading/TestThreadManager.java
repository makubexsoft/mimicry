package org.mimicry.bridge.threading;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.threading.BasicScheduler;
import org.mimicry.bridge.threading.ManagedThread;
import org.mimicry.bridge.threading.ThreadManager;
import org.mimicry.engine.timing.SystemClock;


public class TestThreadManager
{
    private ThreadManager mgr;

    @Before
    public void setUp()
    {
        SimulatorBridge
                .setThreadManager(new ThreadManager(UUID.randomUUID(), new BasicScheduler(new SystemClock())));

        mgr = SimulatorBridge.getThreadManager();
    }

    /**
     * Tests whether the {@link ThreadManager} fires the all-threads-terminated-event when all threads have been
     * terminated.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testAllThreadsTerminated() throws InterruptedException
    {
        ManagedThread thread = new ManagedThread(new Runnable()
        {
            @Override
            public void run()
            {
            }
        });
        thread.start();

        mgr.getShutdownFuture().await(1000);
        assertTrue(mgr.getShutdownFuture().isSuccess());
    }

    /**
     * Tests whether the {@link ThreadManager} fires the all-threads-terminated-event only when really all threads have
     * been terminated.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testNotAllThreadsTerminated() throws InterruptedException
    {
        ManagedThread thread = new ManagedThread(new Runnable()
        {
            @Override
            public void run()
            {
            }
        });

        // spawn second thread
        ManagedThread thread2 = new ManagedThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                }
            }
        });
        thread2.start();
        thread.start();

        mgr.getShutdownFuture().await(1000);
        assertFalse(mgr.getShutdownFuture().isDone());
    }
}
