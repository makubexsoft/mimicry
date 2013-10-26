package org.mimicry.bridge.threading;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mimicry.timing.Timeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A thread scheduler based on checkpoints. A checkpoint is reached when all threads are either:
 * <ol>
 * <li>waiting for a signal on a monitor or</li>
 * <li>waiting to acquire a monitor</li>
 * </ol>
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class CheckpointBasedScheduler extends BasicScheduler
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(CheckpointBasedScheduler.class);
    }
    private final Map<IManagedThread, ThreadState> threadStates;
    private Deque<IManagedThread> monitorAcquireOrder;
    private final Object checkpointSignal = new Object();

    public CheckpointBasedScheduler(Timeline clock)
    {
        super(clock);
        threadStates = new ConcurrentHashMap<IManagedThread, ThreadState>();
        monitorAcquireOrder = new ArrayDeque<IManagedThread>();
    }

    @Override
    public void threadStarted(IManagedThread thread)
    {
        threadStates.put(thread, ThreadState.RUNNING);
        super.threadStarted(thread);
    }

    @Override
    public void threadTerminated(IManagedThread thread)
    {
        super.threadTerminated(thread);
        threadStates.remove(thread);
    }

    @Override
    protected void acquireMonitor(IManagedThread thread, Object monitor, int lockCount)
    {
        threadStates.put(thread, ThreadState.ACQUIRING);

        boolean success = false;
        while (!success)
        {
            synchronized (checkpointSignal)
            {
                if (!checkpointExists())
                {
                    if (checkpointPossible())
                    {
                        createCheckpoint();
                    }
                }

                while (monitorAcquireOrder.peek() != thread)
                {
                    try
                    {
                        checkpointSignal.wait();
                    }
                    catch (InterruptedException e)
                    {
                    }
                }

                IManagedThread owner = getMonitorOwner(monitor);
                if (owner == null || owner == thread)
                {
                    assignOwnership(thread, monitor, lockCount);
                    threadStates.put(thread, ThreadState.RUNNING);
                    success = true;
                }
                else
                {
                    // It might be that the first thread removed its entry and started running
                    // the second thread now receives the signal but the monitor is locked.
                    // So in case the thread state of the monitor owner is RUNNING we need to wait for another signal!
                    while (threadStates.get(owner) == ThreadState.RUNNING)
                    {
                        try
                        {
                            checkpointSignal.wait();
                        }
                        catch (InterruptedException e)
                        {
                        }
                        owner = getMonitorOwner(monitor);
                        if (owner == null)
                        {
                            break;
                        }
                    }
                    if (owner == null || owner == thread)
                    {
                        assignOwnership(thread, monitor, lockCount);
                        threadStates.put(thread, ThreadState.RUNNING);
                        success = true;
                    }
                }

                // notify others that the checkpoint has changed
                monitorAcquireOrder.pop();
                checkpointSignal.notifyAll();
            }
        }
    }

    @Override
    protected void threadEnteredWaitingSet(IManagedThread thread, Object monitor, long timeToWaitInMillis)
    {
        threadStates.put(thread, ThreadState.WAITING);

        synchronized (checkpointSignal)
        {
            if (checkpointPossible() && !checkpointExists())
            {
                createCheckpoint();
            }
        }
    }

    private boolean checkpointExists()
    {
        return (monitorAcquireOrder != null && monitorAcquireOrder.size() > 0);
    }

    private void createCheckpoint()
    {
        createMonitorAcquireOrder();
        synchronized (checkpointSignal)
        {
            checkpointSignal.notifyAll();
        }
        logger.debug("---[ CHECKPOINT ]---");
    }

    @Override
    protected void monitorReleased(Object monitor)
    {
        synchronized (checkpointSignal)
        {
            checkpointSignal.notifyAll();
        }
    }

    @Override
    protected IManagedThread selectThreadToNotify(Set<IManagedThread> waitingThreads)
    {
        List<IManagedThread> threads = new ArrayList<IManagedThread>(waitingThreads);
        Collections.sort(threads, new Comparator<IManagedThread>()
        {
            @Override
            public int compare(IManagedThread t1, IManagedThread t2)
            {
                return t1.getStructuredId().compareTo(t2.getStructuredId());
            }
        });
        return threads.get(0);
    }

    private void createMonitorAcquireOrder()
    {
        List<IManagedThread> sorted = new ArrayList<IManagedThread>(threadStates.keySet());
        Collections.sort(sorted, new Comparator<IManagedThread>()
        {
            @Override
            public int compare(IManagedThread t1, IManagedThread t2)
            {
                return t1.getStructuredId().compareTo(t2.getStructuredId());
            }
        });
        monitorAcquireOrder = new ArrayDeque<IManagedThread>(sorted);
    }

    private boolean checkpointPossible()
    {
        for (ThreadState state : threadStates.values())
        {
            if (state == ThreadState.RUNNING)
            {
                return false;
            }
        }
        return true;
    }
}

enum ThreadState
{
    RUNNING, WAITING, ACQUIRING
}