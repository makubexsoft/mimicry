package org.mimicry.bridge.threading;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.mimicry.timing.Timeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * A basic scheduling implementation that is similar to the one used within the JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class BasicScheduler implements ThreadScheduler
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(BasicScheduler.class);
    }
    /**
     * Contains a list of all monitors and their associated threads with the number of locks currently hold. This list
     * can be used to determine whether a certain thread currently owns a monitor.
     */
    private final Map<Object, MonitorState> monitorOwnerships;
    /**
     * This table contains information on which threads are waiting on a certain monitor. Once the monitors are signaled
     * they will be put into the ownershipRequests list. The numbers within the table are the number of locks the
     * threads already had before they started waiting on the monitors.
     */
    private final Table<Object, IManagedThread, WaitingRecord> waitingThreads;
    /**
     * Synchronization lock to propagate signals on changes to the waiting set.
     */
    private final Object waitingSignalLock = new Object();
    /**
     * Synchronization lock to propagate signals on ownership changes.
     */
    private final Object ownershipSignalLock = new Object();
    /**
     * The clock used to wait for a given time using the simulation timeline.
     */
    private final Timeline clock;

    /**
     * Creates a new scheduler that is based on the timing behavior of the given clock.
     * 
     * @param clock
     *            The clock specifying the timeline behavior.
     */
    public BasicScheduler(Timeline clock)
    {
        Preconditions.checkNotNull(clock);
        this.clock = clock;

        // Important not to use the typical HashMap since
        // the user code might synchronize on mutable data structures
        // which leads to changing hash codes over time so that
        // we no longer can match our monitors!
        monitorOwnerships = new IdentityHashMap<Object, MonitorState>();
        waitingThreads = HashBasedTable.create();
    }

    @Override
    public void threadCreated(IManagedThread thread)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Thread created: " + thread);
        }
    }

    @Override
    public void threadStarted(IManagedThread thread)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Thread started: " + thread);
        }
    }

    @Override
    public void monitorEnter(IManagedThread thread, Object monitor)
    {
        acquireMonitor(thread, monitor, 1 /* lock count */);
    }

    @Override
    public void monitorExit(IManagedThread thread, Object monitor)
    {
        unlock(thread, monitor);
    }

    @Override
    public void monitorWait(IManagedThread thread, Object monitor) throws InterruptedException
    {
        monitorWait(thread, monitor, Long.MAX_VALUE);
    }

    @Override
    public void monitorWait(IManagedThread thread, Object monitor, long timeoutInMillis) throws InterruptedException
    {
        if (!threadOwnsMonitor(thread, monitor))
        {
            throw new IllegalMonitorStateException();
        }

        yieldMonitorOwnership(thread, monitor, timeoutInMillis);
        try
        {
            waitForSignal(thread, monitor, timeoutInMillis);
        }
        finally
        {
            regainMonitorOwnership(thread, monitor);
        }
    }

    @Override
    public void monitorNotify(IManagedThread thread, Object monitor)
    {
        if (!threadOwnsMonitor(thread, monitor))
        {
            throw new IllegalMonitorStateException();
        }

        Map<IManagedThread, WaitingRecord> row = waitingThreads.row(monitor);
        Set<IManagedThread> threads = row.keySet();
        IManagedThread selectedThread = selectThreadToNotify(threads);

        synchronized (waitingSignalLock)
        {
            waitingThreads.get(monitor, selectedThread).signalReceived = true;
            clock.notifyAllOnTarget(waitingSignalLock);
        }
    }

    @Override
    public void monitorNotifyAll(IManagedThread thread, Object monitor)
    {
        if (!threadOwnsMonitor(thread, monitor))
        {
            throw new IllegalMonitorStateException();
        }

        Map<IManagedThread, WaitingRecord> row = waitingThreads.row(monitor);
        synchronized (waitingSignalLock)
        {
            for (WaitingRecord record : row.values())
            {
                record.signalReceived = true;
            }
            clock.notifyAllOnTarget(waitingSignalLock);
        }
    }

    @Override
    public void threadTerminated(IManagedThread thread)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Thread terminated: " + thread);
        }
    }

    /**
     * Blocks until the given thread has acquired the requested number of monitor locks.
     * 
     * @param thread
     *            The thread requesting the monitor.
     * @param monitor
     *            The monitor in question
     * @param lockCount
     *            The number of locks the threads requests. Each time a thread enters the critical section the requested
     *            lock count will be 1 but in case of an awaking thread that already was inside the critical area the
     *            former lock count will be requested again.
     */
    protected void acquireMonitor(IManagedThread thread, Object monitor, int lockCount)
    {
        synchronized (ownershipSignalLock)
        {
            MonitorState monitorState = monitorOwnerships.get(monitor);
            while (monitorState != null && monitorState.owner != null && monitorState.owner != thread)
            {
                try
                {
                    // this time we don't need to use the simulation time
                    // since this behavior is not depending on the simulation itself
                    ownershipSignalLock.wait();
                }
                catch (InterruptedException e)
                {
                }
                monitorState = monitorOwnerships.get(monitor);
            }
            assignOwnership(thread, monitor, lockCount);
        }
    }

    /**
     * A thread has been added to the waiting set. At the time of this invocation the monitor is still owned by the
     * thread and will be released on return.
     * 
     * @param thread
     *            The thread that will now wait.
     * @param monitor
     *            The monitor the thread will wait for a signal on.
     * @param timeToWaitInMillis
     *            The time in milliseconds the thread will wait as long as no signal occurs in the meantime.
     */
    protected void threadEnteredWaitingSet(IManagedThread thread, Object monitor, long timeToWaitInMillis)
    {
    }

    /**
     * The lock counter of the given monitor has reached zero and is now free for allocation by another thread.
     * 
     * @param monitor
     *            The monitor in question.
     */
    protected void monitorReleased(Object monitor)
    {
        synchronized (ownershipSignalLock)
        {
            ownershipSignalLock.notifyAll();
        }
    }

    /**
     * Override to implement a custom notification policy. By default a random thread is chosen to be notified.
     * 
     * @param waitingThreads
     *            All threads currently waiting for a notification on the same monitor.
     * @return The thread that shall be notified.
     */
    protected IManagedThread selectThreadToNotify(Set<IManagedThread> waitingThreads)
    {
        return waitingThreads.iterator().next();
    }

    /**
     * Does the actual assignment of the monitor ownership. If the monitor is already owned by the given thread the lock
     * count will be added to the existing value.
     * 
     * @param thread
     *            The thread to assign the monitor to.
     * @param monitor
     *            The monitor in question.
     * @param lockCount
     *            The number of locks to assign.
     */
    final protected void assignOwnership(IManagedThread thread, Object monitor, int lockCount)
    {
        MonitorState monitorState = monitorOwnerships.get(monitor);
        if (monitorState == null)
        {
            monitorState = new MonitorState();
            monitorState.lockCounter = lockCount;
            monitorState.owner = thread;
            monitorOwnerships.put(monitor, monitorState);
        }
        else if (monitorState.owner == thread)
        {
            monitorState.lockCounter += lockCount;
        }
    }

    final protected IManagedThread getMonitorOwner(Object monitor)
    {
        MonitorState monitorState = monitorOwnerships.get(monitor);
        if (monitorState == null)
        {
            return null;
        }
        return monitorState.owner;
    }

    /**
     * Removes the current monitor ownership from the thread and marks the thread as "waiting for a signal on monitor"
     * in the waiting set.
     * 
     * @param thread
     *            The thread that will wait for a signal.
     * @param monitor
     *            The monitor to wait on for the signal.
     * @param timeToWaitInMillis
     *            The time to wait for a signal in milliseconds.
     */
    private void yieldMonitorOwnership(IManagedThread thread, Object monitor, long timeToWaitInMillis)
    {
        WaitingRecord record = new WaitingRecord();
        record.lockCount = monitorOwnerships.get(monitor).lockCounter;
        waitingThreads.put(monitor, thread, record);
        // it's important to remove ownership after we've created a waiting record
        // otherwise we might miss a signal
        monitorOwnerships.remove(monitor);
        threadEnteredWaitingSet(thread, monitor, timeToWaitInMillis);
        monitorReleased(monitor);
    }

    private boolean threadOwnsMonitor(IManagedThread thread, Object monitor)
    {
        MonitorState monitorState = monitorOwnerships.get(monitor);
        if (monitorState == null)
        {
            return false;
        }
        return monitorState.owner == thread;
    }

    /**
     * Waits for a signal on the given monitor so that the thread can regain its ownership and continue.
     * 
     * @param thread
     *            The thread that will wait.
     * @param monitor
     *            The monitor to wait on for the signal.
     * @param timeToWaitInMillis
     *            The time to wait for a signal in milliseconds.
     */
    private void waitForSignal(IManagedThread thread, Object monitor, long timeToWaitInMillis)
            throws InterruptedException
    {
        synchronized (waitingSignalLock)
        {
            long waitUntil = clock.currentMillis() + timeToWaitInMillis;
            while (!waitingThreads.get(thread, monitor).signalReceived && waitUntil > clock.currentMillis())
            {
                clock.waitOnUntil(waitingSignalLock, waitUntil);
            }
        }
    }

    private void regainMonitorOwnership(IManagedThread thread, Object monitor)
    {
        WaitingRecord waitingRecord = waitingThreads.remove(monitor, thread);
        acquireMonitor(thread, monitor, waitingRecord.lockCount);
    }

    private void unlock(IManagedThread thread, Object monitor)
    {
        MonitorState monitorState = monitorOwnerships.get(monitor);
        if (monitorState.lockCounter == 1)
        {
            monitorOwnerships.remove(monitor);
            monitorReleased(monitor);
        }
        else
        {
            monitorState.lockCounter--;
        }
    }
}

class MonitorState
{
    public IManagedThread owner;
    public int lockCounter;
}

class WaitingRecord
{
    public int lockCount;
    public boolean signalReceived;
}