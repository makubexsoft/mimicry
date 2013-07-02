package com.gc.mimicry.core.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.core.runtime.Application;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockBasedScheduler;
import com.gc.mimicry.core.timing.Scheduler;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.util.ProxyFactory;
import com.gc.mimicry.util.concurrent.ValueFuture;
import com.google.common.base.Preconditions;

/**
 * This is a stack of {@link EventHandler} which define the actual behaviour of the simulated infrastructure.
 * Information is passing the {@link EventStack} as so-called {@link Event}s. When events are passed down in the stack,
 * e.g. from the application to the {@link EventBroker}, they are called downstream events. {@link Event}s that are
 * passed up in the stack, e.g. from the {@link EventBroker} to the {@link Application}, are called upstream events.
 * Each handler within the stack is able to forward, suppress, multiple or demultiplex events. On top of each
 * {@link EventStack} a so-called {@link EventBridge} is located which filters and dispatches events for the
 * {@link Application}s running on the {@link Node}. Events that are not targeted for an application are discarded by
 * the event bridge.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventStack implements EventListener
{
    private final Node node;
    private final List<EventHandler> handlerList;
    private final EventBroker eventBroker;
    private final EventBridge eventBridge;
    private final EventListener brokerListener;

    /**
     * Constructs an empty event stack associated to the given {@link Node} and {@link EventBridge}. This instance will
     * itself register as listener to and passes all downstream events finally to the given {@link EventBroker}.
     * 
     * @param node
     *            The node to associate the event stack with.
     * @param eventBroker
     *            The broker to use for receiving and dispatching downstream events.
     * @param eventBridge
     *            The event bridge to use for dispatching upstream events towards the applications.
     */
    public EventStack(Node node, EventBroker eventBroker, EventBridge eventBridge)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(eventBridge);

        this.node = node;
        this.eventBroker = eventBroker;
        this.eventBridge = eventBridge;

        brokerListener = new EventListener()
        {

            @Override
            public void handleEvent(Event evt)
            {
                sendUpstream(handlerList.size() - 1, evt);
            }
        };
        eventBroker.addEventListener(brokerListener);

        eventBridge.addDownstreamEventListener(this);
        handlerList = new CopyOnWriteArrayList<EventHandler>();
    }

    /**
     * Returns the associated node of this stack.
     * 
     * @return The associated node of this stack.
     */
    public Node getNode()
    {
        return node;
    }

    void sendDownstream(int index, final Event evt)
    {
        if (handlerList.size() > index + 1)
        {
            int nextIndex = index + 1;
            final EventHandler handler = handlerList.get(nextIndex);
            handler.getScheduler().schedule(new Runnable()
            {

                @Override
                public void run()
                {
                    handler.handleDownstream(evt);
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
        else
        {
            // reached bottom
            eventBroker.fireEvent(evt, brokerListener);
        }
    }

    void sendUpstream(int index, final Event evt)
    {
        if (index > 0)
        {
            int nextIndex = index - 1;
            final EventHandler handler = handlerList.get(nextIndex);
            handler.getScheduler().schedule(new Runnable()
            {

                @Override
                public void run()
                {
                    handler.handleUpstream(evt);
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
        else
        {
            // reached top
            eventBridge.dispatchEventToApplication(evt);
        }
    }

    <T extends EventHandler> T findHandler(Class<T> handlerClass)
    {
        EventHandler foundHandler = null;
        for (EventHandler handler : handlerList)
        {
            if (handlerClass.isInstance(handler))
            {
                foundHandler = handler;
                break;
            }
        }
        if (foundHandler == null)
        {
            return null;
        }

        return createProxyHandler(handlerClass, foundHandler);
    }

    /**
     * Creates a proxy around the given handler that executes all method using the {@link Scheduler} of the given
     * {@link EventHandler}.
     * 
     * @param handlerClass
     *            The publicly visible class or interface of the proxy.
     * @param handler
     *            The original handler
     * @return A proxy that executes all method invocations using the {@link Scheduler} of the original
     *         {@link EventHandler}.
     */
    private <T extends EventHandler> T createProxyHandler(Class<T> handlerClass, final EventHandler handler)
    {
        return handlerClass.cast(ProxyFactory.createProxy(handlerClass.getClassLoader(), handlerClass,
                new InvocationHandler()
                {

                    @Override
                    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
                    {
                        ValueFuture<Object> future = handler.getScheduler().schedule(new Callable<Object>()
                        {
                            @Override
                            public Object call() throws Exception
                            {
                                return method.invoke(handler, args);
                            }
                        }, 0, TimeUnit.MILLISECONDS);

                        future.awaitUninterruptibly(Long.MAX_VALUE);
                        if (future.isSuccess())
                        {
                            return future.getValue();
                        }
                        throw new RuntimeException(future.getCause());
                    }
                }));
    }

    void addHandler(EventHandler handler)
    {
        handlerList.add(handler);
    }

    @Override
    public void handleEvent(Event evt)
    {
        sendDownstream(-1, evt);
    }

    /**
     * Initializes all {@link EventHandler}s within this stack.
     * 
     * @param clock
     */
    public void init(Clock clock)
    {
        int index = 0;
        for (EventHandler handler : handlerList)
        {
            // Jobs of a single event handler are executed in a single thread.
            // Event passing to the handler should be done through the scheduler
            // by doing so the event handler is not forced to be concerned about multi-threading issues.
            handler.init(new EventHandlerContext(this, index), new ClockBasedScheduler(clock), clock);

            index++;
        }
    }
}
