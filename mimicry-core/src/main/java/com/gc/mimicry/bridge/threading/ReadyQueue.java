package com.gc.mimicry.bridge.threading;

import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

public class ReadyQueue implements Iterable<Object>
{
    private Map<Object, Deque<IManagedThread>> waitingThreads;

    public ReadyQueue(MonitorMap monitorMap)
    {

    }

    public boolean threadIsReady(IManagedThread thread, Object monitor)
    {
        Deque<IManagedThread> set = waitingThreads.get(monitor);
        if (set == null)
        {
            return false;
        }
        return set.peekFirst() == thread;
    }

    public void remove(IManagedThread thread, Object monitor)
    {
        Deque<IManagedThread> set = waitingThreads.get(monitor);
        set.pop();
    }

    @Override
    public Iterator<Object> iterator()
    {
        return waitingThreads.keySet().iterator();
    }
}
