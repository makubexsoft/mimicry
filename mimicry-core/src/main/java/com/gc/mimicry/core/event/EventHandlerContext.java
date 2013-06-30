package com.gc.mimicry.core.event;

import com.google.common.base.Preconditions;

public class EventHandlerContext
{
    private EventStack stack;
    private int handlerIndex;

    EventHandlerContext(EventStack stack, int handlerIndex)
    {
        Preconditions.checkNotNull(stack);
        this.stack = stack;
        this.handlerIndex = handlerIndex;
    }

    public EventStack getEventStack()
    {
        return stack;
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
