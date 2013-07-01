package com.gc.mimicry.core.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.gc.mimicry.core.runtime.Node;
import com.gc.mimicry.shared.events.Event;
import com.google.common.base.Preconditions;

public class EventStack implements EventListener
{
    private final Node node;
    private final List<EventHandler> handlerList;
    private final EventBroker eventBroker;
    private final EventBridge eventBridge;

    public EventStack(Node node, EventBroker eventBroker, EventBridge eventBridge)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(eventBridge);

        this.node = node;
        this.eventBroker = eventBroker;
        this.eventBridge = eventBridge;

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
            final EventHandlerContext ctx = new EventHandlerContext(this, nextIndex);
            final EventHandler handler = handlerList.get(nextIndex);
            handler.getScheduler().schedule(new Runnable()
            {

                @Override
                public void run()
                {
                    handler.handleDownstream(ctx, evt);
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
        else
        {
            // reached bottom
            eventBroker.fireEvent(evt);
        }
    }

    void sendUpstream(int index, final Event evt)
    {
        if (index > 0)
        {
            int nextIndex = index - 1;
            final EventHandlerContext ctx = new EventHandlerContext(this, nextIndex);
            final EventHandler handler = handlerList.get(nextIndex);
            handler.getScheduler().schedule(new Runnable()
            {

                @Override
                public void run()
                {
                    handler.handleUpstream(ctx, evt);
                }
            }, 0, TimeUnit.MILLISECONDS);
        }
        else
        {
            // reached top
            eventBridge.dispatchEventToApplication(evt);
        }
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
    public void eventOccurred(Event evt)
    {
        sendDownstream(-1, evt);
    }
}
