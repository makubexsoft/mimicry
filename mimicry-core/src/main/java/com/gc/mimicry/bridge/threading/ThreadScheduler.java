package com.gc.mimicry.bridge.threading;

public interface ThreadScheduler
{
    public void threadCreated(IManagedThread thread);

    public void threadStarted(IManagedThread thread);

    public void monitorEnter(IManagedThread thread, Object monitor);

    public void monitorExit(IManagedThread thread, Object monitor);

    public void monitorWait(IManagedThread thread, Object monitor) throws InterruptedException;

    public void monitorWait(IManagedThread thread, Object monitor, long timeoutInMillis) throws InterruptedException;

    public void monitorNotify(IManagedThread thread, Object monitor);

    public void monitorNotifyAll(IManagedThread thread, Object monitor);

    public void threadTerminated(IManagedThread thread);
}
