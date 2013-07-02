package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.Event;
import com.google.common.base.Preconditions;

public class EventHandlerContext
{
    private final EventStack stack;
    private final int handlerIndex;

    EventHandlerContext(EventStack stack, int handlerIndex)
    {
        Preconditions.checkNotNull(stack);
        this.stack = stack;
        this.handlerIndex = handlerIndex;
    }

    public <T extends EventHandler> T findHandler(Class<T> handlerClass)
    {
        return stack.findHandler(handlerClass);
    }

    public void sendDownstream(Event evt)
    {
        stack.sendDownstream(handlerIndex, evt);
    }

    public void sendUpstream(Event evt)
    {
        stack.sendUpstream(handlerIndex, evt);
    }
}
