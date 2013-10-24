package com.gc.mimicry.engine.local;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.cep.CEPEngine;
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
    private final CEPEngine eventBroker;
    private final Timeline timeline;
    private final ClassLoader eventHandlerLoader;
    private final ApplicationRepository appRepo;

    public NodeFactory(CEPEngine eventEngine, Timeline timeline, ClassLoader eventHandlerLoader,
            ApplicationRepository appRepo)
    {
        Preconditions.checkNotNull(eventEngine);
        Preconditions.checkNotNull(timeline);
        Preconditions.checkNotNull(eventHandlerLoader);
        Preconditions.checkNotNull(appRepo);

        this.eventBroker = eventEngine;
        this.timeline = timeline;
        this.eventHandlerLoader = eventHandlerLoader;
        this.appRepo = appRepo;
    }

    public Timeline getTimeline()
    {
        return timeline;
    }

    public LocalNode createNode(NodeParameters descriptor, File sessionDir)
    {
        File nodeDir = new File(sessionDir, descriptor.getNodeName());
        nodeDir.mkdirs();

        LocalNode node = new LocalNode(descriptor.getNodeName(), eventBroker, timeline, appRepo, nodeDir);
        initEventStack(node, descriptor);
        return node;
    }

    private void initEventStack(LocalNode node, NodeParameters descriptor)
    {
        EventStack eventStack = node.getEventStack();
        for (EventHandlerParameters handlerParams : descriptor.getEventStack())
        {
            EventHandler handler = createHandler(eventStack, handlerParams);
            if (handler != null)
            {
                eventStack.addHandler(handler);
            }
        }
        eventStack.init(timeline);
    }

    private EventHandler createHandler(EventStack eventStack, EventHandlerParameters handlerParams)
    {
        EventHandler handler = newHandler(handlerParams.getClassName());
        if (handler instanceof Configurable)
        {
            Configurable configurable = (Configurable) handler;
            configurable.configure(handlerParams.getConfiguration());
        }
        return handler;
    }

    private EventHandler newHandler(String fullQualifiedClassName)
    {
        Class<EventHandler> handlerClass = findClass(fullQualifiedClassName);
        if (handlerClass == null)
        {
            return null;
        }

        try
        {
            return handlerClass.newInstance();
        }
        catch (Exception e)
        {
            logger.error("Failed to create event handler of type: " + fullQualifiedClassName, e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Class<EventHandler> findClass(String name)
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
