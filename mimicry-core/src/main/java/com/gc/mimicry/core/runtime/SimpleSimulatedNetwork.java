package com.gc.mimicry.core.runtime;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.core.event.SimpleEventBroker;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.DiscreteClock;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.util.concurrent.DefaultFuture;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

public class SimpleSimulatedNetwork implements SimulatedNetwork
{
    private boolean initialized;
    private final Future<?> endFuture;
    private final EventBroker eventBroker;
    private NodeManager nodeMgr;
    private Clock clock;
    private final ClassLoadingContext ctx;
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(SimpleSimulatedNetwork.class);
    }

    public SimpleSimulatedNetwork(ClassLoadingContext ctx)
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
    }

    @Override
    public NodeRef spawnNode(NodeConfiguration nodeConfig)
    {
        checkInitialized();
        logger.info("Spawning node in local JVM: " + nodeConfig.getNodeName());
        Node node = nodeMgr.createNode(nodeConfig);
        return new LocalNodeRef(node);
    }

    @Override
    public ApplicationRef spawnApplication(NodeRef nodeRef, ApplicationDescriptor appDesc)
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
    public void startApplication(ApplicationRef app)
    {
        checkInitialized();

        Node node = nodeMgr.findNode(app.getNodeId());
        if (node == null)
        {
            throw new RuntimeException("Node doesn't exist in local JVM: " + app.getNodeId());
        }

        ApplicationManager manager = node.getApplicationManager();
        Application application = manager.getApplication(app.getApplicationId());
        logger.info("Starting application: " + app);
        application.start();
    }

    @Override
    public Future<?> shutdown()
    {
        checkInitialized();
        logger.info("Shutting simulation down...");
        // TODO Auto-generated method stub
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
        checkInitialized();
        return eventBroker;
    }

    @Override
    public Clock getClock()
    {
        checkInitialized();
        return clock;
    }

    private void checkInitialized()
    {
        if (!initialized)
        {
            throw new IllegalStateException("Network not initialized. Did you miss to invoke init?");
        }
    }
}
