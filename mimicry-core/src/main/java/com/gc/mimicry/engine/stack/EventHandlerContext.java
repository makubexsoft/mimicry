package com.gc.mimicry.engine.stack;

import com.gc.mimicry.engine.event.Event;
import com.google.common.base.Preconditions;

/**
 * The context object is used to act as mediator between the {@link EventHandler} and the {@link EventStack}. It
 * contains some meta-information about the position of the {@link EventHandler} within the {@link EventStack} that is
 * used to determine which the next higher and lower handler are.
 * 
 * @author Marc-Christian Schulze
 * 
 */
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
