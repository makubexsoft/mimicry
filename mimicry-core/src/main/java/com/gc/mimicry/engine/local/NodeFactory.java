package com.gc.mimicry.engine.local;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.stack.Configurable;
import com.gc.mimicry.engine.stack.EventHandler;
import com.gc.mimicry.engine.stack.EventHandlerParameters;
import com.gc.mimicry.engine.stack.EventStack;
import com.gc.mimicry.engine.timing.Timeline;
import com.google.common.base.Preconditions;

public class NodeFactory
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(NodeFactory.class);
    }
    private final EventEngine eventBroker;
    private final Timeline clock;
    private final ClassLoader eventHandlerLoader;
    private final ApplicationRepository appRepo;

    public NodeFactory(EventEngine eventBroker, Timeline clock, ClassLoader eventHandlerLoader,
            ApplicationRepository appRepo)
    {
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(clock);
        Preconditions.checkNotNull(eventHandlerLoader);
        Preconditions.checkNotNull(appRepo);

        this.eventBroker = eventBroker;
        this.clock = clock;
        this.eventHandlerLoader = eventHandlerLoader;
        this.appRepo = appRepo;
    }

    public LocalNode createNode(NodeParameters descriptor, File sessionDir)
    {
        File nodeDir = new File(sessionDir, descriptor.getNodeName());
        nodeDir.mkdirs();

        LocalNode node = new LocalNode(descriptor.getNodeName(), eventBroker, clock, appRepo, nodeDir);
        initEventStack(node, descriptor);
        return node;
    }

    private void initEventStack(LocalNode node, NodeParameters descriptor)
    {
        EventStack eventStack = node.getEventStack();
        for (EventHandlerParameters handlerParams : descriptor.getEventStack())
        {
            loadHandler(eventStack, handlerParams);
        }
        eventStack.init(clock);
    }

    private void loadHandler(EventStack eventStack, EventHandlerParameters handlerParams)
    {
        EventHandler handler = createHandler(handlerParams.getClassName());
        if (handler != null)
        {
            if (handler instanceof Configurable)
            {
                Configurable configurable = (Configurable) handler;
                configurable.configure(handlerParams.getConfiguration());
            }
            eventStack.addHandler(handler);
        }
    }

    private EventHandler createHandler(String name)
    {
        Class<EventHandler> handlerClass = loadHandlerClass(name);
        if (handlerClass == null)
        {
            return null;
        }
        try
        {
            return handlerClass.newInstance();
        }
        catch (InstantiationException e)
        {
            logger.error("Failed to create event handler of type: " + name, e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("Failed to create event handler of type: " + name, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<EventHandler> loadHandlerClass(String name)
    {
        try
        {
            Class<?> loadedClass = eventHandlerLoader.loadClass(name);
            if (!EventHandler.class.isAssignableFrom(loadedClass))
            {
                return null;
            }
            return (Class<EventHandler>) loadedClass;
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Failed to load event handler class: " + name, e);
            return null;
        }
    }
}
