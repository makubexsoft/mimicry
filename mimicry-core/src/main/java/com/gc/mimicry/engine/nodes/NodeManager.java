package com.gc.mimicry.engine.nodes;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.EventBroker;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.nodes.events.CreateNodeEvent;
import com.gc.mimicry.engine.nodes.events.NodeCreatedEvent;
import com.gc.mimicry.engine.stack.Configurable;
import com.gc.mimicry.engine.stack.EventHandler;
import com.gc.mimicry.engine.stack.EventHandlerConfiguration;
import com.gc.mimicry.engine.stack.EventStack;
import com.gc.mimicry.engine.timing.Clock;
import com.google.common.base.Preconditions;

/**
 * A node factory is able to create new nodes that run their applications in the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class NodeManager implements Closeable, EventListener
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(NodeManager.class);
    }
    private final Map<UUID, Node> nodes;
    private final EventBroker eventBroker;
    private final ClassPathConfiguration ctx;
    private final Clock clock;

    public NodeManager(ClassPathConfiguration ctx, EventBroker eventBroker, Clock clock)
    {
        Preconditions.checkNotNull(ctx);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(clock);

        this.ctx = ctx;
        this.eventBroker = eventBroker;
        this.clock = clock;

        eventBroker.addEventListener(this);

        nodes = new HashMap<UUID, Node>();
    }

    public Node findNode(NodeRef ref)
    {
        return nodes.get(ref.getNodeId());
    }

    public Node findNode(UUID id)
    {
        return nodes.get(id);
    }

    public Node createNode(NodeConfiguration descriptor)
    {
        Node node = new Node(ctx, descriptor.getNodeName(), eventBroker, clock);
        initEventStack(node, descriptor);
        nodes.put(node.getId(), node);
        eventBroker.fireEvent(new NodeCreatedEvent(new RemoteNodeRef(node.getId())), this);
        logger.info("Node created: " + descriptor.getNodeName() + "/" + node.getId());
        return node;
    }

    private void initEventStack(Node node, NodeConfiguration descriptor)
    {
        EventStack eventStack = node.getEventStack();
        for (EventHandlerConfiguration handlerConfig : descriptor.getEventStack())
        {
            EventHandler handler = createHandler(handlerConfig.getClassName());
            if (handler != null)
            {
                if (handler instanceof Configurable)
                {
                    Configurable configurable = (Configurable) handler;
                    configurable.configure(handlerConfig.getConfiguration());
                }
                eventStack.addHandler(handler);
            }
        }
        eventStack.init(clock);
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
            Class<?> loadedClass = ctx.getEventHandlerClassLoader().loadClass(name);
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

    @Override
    public void handleEvent(Event evt)
    {
        if (evt instanceof CreateNodeEvent)
        {
            CreateNodeEvent e = (CreateNodeEvent) evt;
            createNode(e.getNodeConfig());
        }
    }

    @Override
    public void close() throws IOException
    {
        eventBroker.removeEventListener(this);
    }
}
