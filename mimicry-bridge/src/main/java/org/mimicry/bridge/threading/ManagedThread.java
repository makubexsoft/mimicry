package org.mimicry.bridge.threading;

import java.util.concurrent.CopyOnWriteArrayList;

import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.threading.IManagedThread;
import org.mimicry.bridge.threading.ThreadShutdownListener;
import org.mimicry.engine.DefaultEventFactory;
import org.mimicry.engine.EventFactory;
import org.mimicry.engine.Identity;
import org.mimicry.util.ExceptionUtil;
import org.mimicry.util.StructuredId;


/**
 * Subclasses of this class provide a graceful termination strategy and are used to clean up a simulated application
 * asynchronously.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedThread extends Thread implements IManagedThread
{
    /**
     * Override of the original constructor {@link Thread#Thread()} which is invoked by the subclass.
     */
    public ManagedThread()
    {
        super();
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(Runnable, String)} which is invoked by the subclass.
     */
    public ManagedThread(Runnable target, String name)
    {
        super(target, name);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(Runnable)} which is invoked by the subclass.
     * 
     * @see Thread#Thread()
     */
    public ManagedThread(Runnable target)
    {
        super(target);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(String)} which is invoked by the subclass.
     * 
     * @see Thread#Thread(String)
     */
    public ManagedThread(String name)
    {
        super(name);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(ThreadGroup, Runnable, String, long)} which is invoked
     * by the subclass.
     * 
     * @see Thread#Thread(ThreadGroup, Runnable, String, long)
     */
    public ManagedThread(ThreadGroup group, Runnable target, String name, long stackSize)
    {
        super(group, target, name, stackSize);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(ThreadGroup, Runnable, String)} which is invoked by the
     * subclass.
     * 
     * @see Thread#Thread(ThreadGroup, Runnable, String)
     */
    public ManagedThread(ThreadGroup group, Runnable target, String name)
    {
        super(group, target, name);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(ThreadGroup, Runnable)} which is invoked by the
     * subclass.
     * 
     * @see Thread#Thread(ThreadGroup, Runnable)
     */
    public ManagedThread(ThreadGroup group, Runnable target)
    {
        super(group, target);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    /**
     * Override of the original constructor {@link Thread#Thread(ThreadGroup, String)} which is invoked by the subclass.
     * 
     * @see Thread#Thread(ThreadGroup, String)
     */
    public ManagedThread(ThreadGroup group, String name)
    {
        super(group, name);
        init();
        SimulatorBridge.getThreadManager().threadCreated(this);
    }

    public static ManagedThread currentThread()
    {
        return (ManagedThread) Thread.currentThread();
    }

    @Override
    public void run()
    {
        try
        {
            super.run();
            SimulatorBridge.getThreadManager().threadTerminated(this);
        }
        catch (Throwable th)
        {
            SimulatorBridge.getThreadManager().threadTerminated(this, th);
            ExceptionUtil.throwUnchecked(th);
        }
    }

    @Override
    public StructuredId getStructuredId()
    {
        return id;
    }

    @Override
    public boolean isShuttingDown()
    {
        return shuttingDown;
    }

    @Override
    public void shutdownGracefully()
    {
        shuttingDown = true;
        // interrupt();
        fireThreadShouldTerminate();
        // suspend();
        stop();
    }

    @Override
    public void addThreadShutdownListener(ThreadShutdownListener l)
    {
        listener.add(l);
    }

    @Override
    public void removeThreadShutdownListener(ThreadShutdownListener l)
    {
        listener.remove(l);
    }

    private void fireThreadShouldTerminate()
    {
        for (ThreadShutdownListener l : listener)
        {
            l.threadShouldTerminate(this);
        }
    }

    private void init()
    {
        super.setDaemon(true);
        identity = Identity.create("ManagedThread-" + getName());
        eventFactory = DefaultEventFactory.create(getIdentity());
        listener = new CopyOnWriteArrayList<ThreadShutdownListener>();
        assignId();
    }

    private void assignId()
    {
        Thread thread = Thread.currentThread();
        if (thread instanceof ManagedThread)
        {
            IManagedThread managedThread = (IManagedThread) thread;
            id = managedThread.getStructuredId().createSubId();
        }
        else
        {
            id = new StructuredId();
        }
    }

    @Override
    public String toString()
    {
        return "Managed" + super.toString();
    }

    @Override
    public Identity getIdentity()
    {
        return identity;
    }

    @Override
    public EventFactory getEventFactory()
    {
        return eventFactory;
    }

    private EventFactory eventFactory;
    private Identity identity;
    private volatile boolean shuttingDown;
    private StructuredId id;
    private CopyOnWriteArrayList<ThreadShutdownListener> listener;

}
