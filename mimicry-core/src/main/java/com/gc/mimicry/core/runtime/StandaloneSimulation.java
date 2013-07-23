package com.gc.mimicry.core.runtime;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationBundleDescriptor;
import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.core.event.Node;
import com.gc.mimicry.core.event.NodeManager;
import com.gc.mimicry.core.event.SimpleEventBroker;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.DiscreteClock;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.core.timing.net.ClockDriver;
import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

/**
 * Simple implementation of a simulated network that is used in standalone and unit-test setups.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class StandaloneSimulation implements Simulation
{
    public StandaloneSimulation(ClassLoadingContext ctx)
    {
        Preconditions.checkNotNull(ctx);

        this.ctx = ctx;

        endFuture = new DefaultFuture();
        eventBroker = new SimpleEventBroker();
    }

    @Override
    public void init(NetworkConfiguration config)
    {
        logger.info("Initializing network: " + config);
        this.config = config;

        createClock(config);
        nodeMgr = new NodeManager(ctx, eventBroker, clock);

        initialized = true;
    }

    private void createClock(NetworkConfiguration config)
    {
        switch (config.getClockType())
        {
            case REALTIME:
                clock = new RealtimeClock(config.getInitialTimeMillis());
                break;

            case DISCRETE:
                clock = new DiscreteClock(config.getInitialTimeMillis());
                break;

            default:
                throw new RuntimeException("Invalid clock type: " + config.getClockType());
        }
        clockDriver = new ClockDriver(eventBroker, clock);
    }

    @Override
    public NodeRef createNode(String nodeName, EventHandlerConfiguration[] eventStack)
    {
        checkInitialized();
        logger.info("Spawning node in local JVM: " + nodeName);
        Node node = nodeMgr.createNode(new NodeConfiguration(nodeName, eventStack));
        return new LocalNodeRef(node);
    }

    @Override
    public ApplicationRef loadApplication(NodeRef nodeRef, ApplicationBundleDescriptor appDesc)
    {
        checkInitialized();

        Node node = nodeMgr.findNode(nodeRef);
        if (node == null)
        {
            throw new RuntimeException("Node doesn't exist in local JVM: " + nodeRef);
        }

        ApplicationManager manager = node.getApplicationManager();
        try
        {
            logger.info("Spawning application in local JVM: " + appDesc.getName() + " on node: " + nodeRef);
            Application application = manager.launchApplication(appDesc);
            return new LocalApplicationRef(application);
        }
        catch (IOException e)
        {
            logger.error("Failed to spawn application.", e);
            throw new RuntimeException("Failed to spawn application.", e);
        }
    }

    @Override
    public NetworkConfiguration getConfig()
    {
        return config;
    }

    @Override
    public Future<?> shutdown()
    {
        checkInitialized();
        logger.info("Shutting simulation down...");
        // TODO shutdown all applications
        return endFuture;
    }

    @Override
    public Future<?> getSimulationEndFuture()
    {
        return endFuture;
    }

    @Override
    public EventBroker getEventBroker()
    {
        return eventBroker;
    }

    private void checkInitialized()
    {
        if (!initialized)
        {
            throw new IllegalStateException("Network not initialized. Did you miss to invoke init?");
        }
    }

    private ClockDriver clockDriver;
    private boolean initialized;
    private NodeManager nodeMgr;
    private Clock clock;
    private NetworkConfiguration config;
    private final Future<?> endFuture;
    private final EventBroker eventBroker;
    private final ClassLoadingContext ctx;
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(StandaloneSimulation.class);
    }

}
