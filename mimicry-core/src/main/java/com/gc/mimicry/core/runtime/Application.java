package com.gc.mimicry.core.runtime;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.core.event.Node;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * Instances of this class represent a simulated application instance within the current JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Application implements Closeable
{
    private final UUID id;
    private final Node node;
    private final ApplicationBridge bridge;
    private final Future<?> terminationFuture;
    private final Clock clock;

    /**
     * Creates a new application instance and initializes the given {@link ApplicationBridge}. However, the application
     * is not started.
     * 
     * @param node
     * @param bridge
     */
    Application(Node node, ApplicationBridge bridge)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(bridge);

        this.node = node;
        this.bridge = bridge;
        this.clock = node.getClock();
        id = UUID.randomUUID();

        bridge.setApplicationId(id);
        bridge.setClock(clock);
        bridge.setEventBridge(node.getEventBridge());

        terminationFuture = bridge.getShutdownFuture();
    }

    /**
     * Returns a future that can be observed to get notified when this application terminates regardless of the reason.
     * 
     * @return
     */
    public Future<?> getTerminationFuture()
    {
        return terminationFuture;
    }

    /**
     * Returns a reference to the clock this application uses which is the same as of the {@link Node} this applications
     * runs on.
     * 
     * @return
     */
    public Clock getClock()
    {
        return clock;
    }

    /**
     * The node this application runs on.
     * 
     * @return
     */
    public Node getNode()
    {
        return node;
    }

    /**
     * Starts the main thread of the application.
     */
    public void start()
    {
        bridge.startApplication();
    }

    /**
     * Shuts the application down and returns the shutdown future that can be observed.
     * 
     * @return
     */
    public Future<?> stop()
    {
        bridge.shutdownApplication();
        return terminationFuture;
    }

    /**
     * Returns the id of the application used to identify the application within the simulation.
     * 
     * @return
     */
    public UUID getId()
    {
        return id;
    }

    @Override
    public void close()
    {
        stop();
    }
}
