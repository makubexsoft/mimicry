package com.gc.mimicry.core.event;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.core.runtime.Node;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockBasedScheduler;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.util.ProxyFactory;
import com.gc.mimicry.util.concurrent.ValueFuture;
import com.google.common.base.Preconditions;

public class EventStack implements EventListener
{
    private final Node node;
    private final List<EventHandler> handlerList;
    private final EventBroker eventBroker;
    private final EventBridge eventBridge;
    private final EventListener brokerListener;

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
        EventHandler h = null;
        for (EventHandler handler : handlerList)
        {
            if (handlerClass.isInstance(handler))
            {
                h = handler;
                break;
            }
        }
        if (h == null)
        {
            return null;
        }

        final EventHandler handler = h;

        return handlerClass.cast(ProxyFactory.createProxy(handlerClass.getClassLoader(), handlerClass,
                new InvocationHandler()
                {

                    @Override
                    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable
                    {
                        ValueFuture<Object> future = handler.getScheduler().schedule(new Callable<Object>()
                        {
                            @Override
                            public Object call()
                            {
                                try
                                {
                                    return method.invoke(handler, args);
                                }
                                catch (IllegalAccessException e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                catch (IllegalArgumentException e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                catch (InvocationTargetException e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                return null;
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

    public void addHandler(EventHandler handler)
    {
        handlerList.add(handler);
    }

    public void insertHandler(int index, EventHandler handler)
    {
        handlerList.add(index, handler);
    }

    public void removeHandler(EventHandler handler)
    {
        handlerList.remove(handler);
    }

    public EventHandler removeHandler(Class<EventHandler> handlerClass)
    {
        EventHandler h = null;
        for (EventHandler handler : handlerList)
        {
            if (handlerClass.isInstance(handler))
            {
                h = handler;
                break;
            }
        }
        if (h != null)
        {
            handlerList.remove(h);
        }
        return h;
    }

    @Override
    public void handleEvent(Event evt)
    {
        sendDownstream(-1, evt);
    }

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
