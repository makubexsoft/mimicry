package com.gc.mimicry.bridge.threading;

import com.gc.mimicry.bridge.SimulatorBridge;

/**
 * Static interception point that is invoked by the aspects and replace monitor byte code instructions to implement
 * custom thread scheduling.
 * 
 * @author Marc-Christian Schulze
 * @see ThreadScheduler
 */
public class MonitorInterceptor
{
    public static void monitorEnter(Object monitor)
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorEnter(thread, monitor);
    }

    public static void monitorExit(Object monitor)
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorExit(thread, monitor);
    }

    public static void monitorWait(Object monitor) throws InterruptedException
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorWait(thread, monitor);
    }

    public static void monitorWait(Object monitor, long timeoutInMillis) throws InterruptedException
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorWait(thread, monitor, timeoutInMillis);
    }

    public static void monitorNotify(Object monitor)
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorNotify(thread, monitor);
    }

    public static void monitorNotifyAll(Object monitor)
    {
        ManagedThread thread = ManagedThread.currentThread();
        SimulatorBridge.getThreadManager().getScheduler().monitorNotifyAll(thread, monitor);
    }
}
