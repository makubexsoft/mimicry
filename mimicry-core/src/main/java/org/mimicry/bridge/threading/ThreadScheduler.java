package org.mimicry.bridge.threading;

/**
 * Implementing this interface you can provide your custom thread scheduling algorithm.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ThreadScheduler
{
    /**
     * A new thread has been created in the application.
     * 
     * @param thread
     *            The thread that has been created.
     */
    public void threadCreated(IManagedThread thread);

    /**
     * A thread has been started. This method gets invoked in the context of the parent thread once
     * {@link Thread#start()} was called.
     * 
     * @param thread
     *            The thread that has been started.
     */
    public void threadStarted(IManagedThread thread);

    /**
     * A thread wants to enter a critical section protected by the given monitor. This method shall block until the
     * thread has acquired the monitor ownership.
     * 
     * @param thread
     *            The thread that wants to enter the critical section.
     * @param monitor
     *            The monitor that the thread has requested using a synchronized() block.
     */
    public void monitorEnter(IManagedThread thread, Object monitor);

    /**
     * A thread leaves a critical section.
     * 
     * @param thread
     *            The thread that leaves the critical section.
     * @param monitor
     *            The monitor that was used to protect the critical section using a synchronized() block.
     */
    public void monitorExit(IManagedThread thread, Object monitor);

    /**
     * A thread invoked {@link Object#wait()} on the given monitor. This method shall block until either:
     * <ol>
     * <li>a signal is raised on the monitor (e.g. using {@link Object#notify()} or {@link Object#notifyAll()}</li>
     * <li>another thread interrupts (cf. {@link Thread#interrupt()})</li>
     * </ol>
     * 
     * @param thread
     *            The thread that wants to wait for the signal.
     * @param monitor
     *            The monitor used to wait.
     * @throws InterruptedException
     *             If another thread interrupted the given thread while waiting.
     */
    public void monitorWait(IManagedThread thread, Object monitor) throws InterruptedException;

    /**
     * A thread invoked {@link Object#wait(long)} on the given monitor. This method shall block until either:
     * <ol>
     * <li>a signal is raised on the monitor (e.g. using {@link Object#notify()} or {@link Object#notifyAll()}</li>
     * <li>another thread interrupts (cf. {@link Thread#interrupt()})</li>
     * <li>the timeout expires</li>
     * </ol>
     * 
     * @param thread
     *            The thread that wants to wait for the signal.
     * @param monitor
     *            The monitor used to wait.
     * @param timeoutInMillis
     *            The time to wait in milliseconds.
     * @throws InterruptedException
     *             If another thread interrupted the given thread while waiting.
     */
    public void monitorWait(IManagedThread thread, Object monitor, long timeoutInMillis) throws InterruptedException;

    /**
     * A thread invoke {@link Object#notify()} on the given monitor.
     * 
     * @param thread
     *            The thread that invoked notify.
     * @param monitor
     *            The monitor on which notify was invoked.
     */
    public void monitorNotify(IManagedThread thread, Object monitor);

    /**
     * A thread invoke {@link Object#notifyAll()} on the given monitor.
     * 
     * @param thread
     *            The thread that invoked notifyAll.
     * @param monitor
     *            The monitor on which notifyAll was invoked.
     */
    public void monitorNotifyAll(IManagedThread thread, Object monitor);

    /**
     * The given thread has been terminated.
     * 
     * @param thread
     *            The thread that has been terminated.
     */
    public void threadTerminated(IManagedThread thread);
}
