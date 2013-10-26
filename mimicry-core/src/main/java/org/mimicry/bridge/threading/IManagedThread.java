package org.mimicry.bridge.threading;

import org.mimicry.engine.EventFactory;
import org.mimicry.engine.Identity;
import org.mimicry.util.StructuredId;


/**
 * Abstraction of a managed thread to decouple the core from the bridge package.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface IManagedThread
{
    /**
     * Returns a structured id that reflects the causal order in which the thread has been created.
     * 
     * @return
     */
    public StructuredId getStructuredId();

    /**
     * Returns whether the shutdown flag has been set for this thread.
     * 
     * @return
     */
    public boolean isShuttingDown();

    /**
     * Terminates this thread gracefully.
     */
    public void shutdownGracefully();

    /**
     * Adds a {@link ThreadShutdownListener} that gets notified when the thread has terminated.
     * 
     * @param l
     */
    public void addThreadShutdownListener(ThreadShutdownListener l);

    /**
     * Removes the given listener.
     * 
     * @param l
     */
    public void removeThreadShutdownListener(ThreadShutdownListener l);

    public Identity getIdentity();

    public EventFactory getEventFactory();

    public String getName();
}
