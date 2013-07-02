package com.gc.mimicry.core.event;

import com.gc.mimicry.core.runtime.Application;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;
import com.gc.mimicry.shared.events.Event;
import com.google.common.base.Preconditions;

/**
 * Base class for most of the {@link EventHandler}s.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventHandlerBase implements EventHandler
{
    @Override
    final public void init(EventHandlerContext ctx, Scheduler scheduler, Clock clock)
    {
        Preconditions.checkNotNull(ctx);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(clock);

        context = ctx;
        this.scheduler = scheduler;
        this.clock = clock;

        initHandler();
    }

    @Override
    final public Scheduler getScheduler()
    {
        return scheduler;
    }

    final public Clock getClock()
    {
        return clock;
    }

    final public EventHandlerContext getContext()
    {
        return context;
    }

    /**
     * Override to handle events passed down the {@link EventStack}.
     * 
     * @param evt
     */
    @Override
    public void handleDownstream(Event evt)
    {
        context.sendDownstream(evt);
    }

    /**
     * Override to handle events passed up in the {@link EventStack}.
     * 
     * @param evt
     */
    @Override
    public void handleUpstream(Event evt)
    {
        context.sendUpstream(evt);
    }

    /**
     * Send the given event to the next event handler upstream in the {@link EventStack}. Once the top of the
     * {@link EventStack} is reached the event is dispatched to the application identified by the id within the
     * {@link Event#getTargetApplication()} attribute. If no such application exists the event is dropped. If you
     * override this method make sure that you pass all events not of your interest upstream. Otherwise you would
     * suppress the event.
     * 
     * @param evt
     *            The event received either from an {@link EventHandler} higher in the {@link EventStack} or one of the
     *            {@link Application} running this {@link Node}.
     */
    protected void sendUpstream(Event evt)
    {
        context.sendUpstream(evt);
    }

    /**
     * Send the given event to the next event handler downstream in the {@link EventStack}. Once the bottom of the
     * {@link EventStack} is reached the event is dispatched using the {@link EventBroker} to the event stacks of all
     * other nodes.If you override this method make sure that you pass all events not of your interest downstream.
     * Otherwise you would suppress the event.
     * 
     * @param evt
     *            The event received either from an {@link EventHandler} lower in the {@link EventStack} or the
     *            {@link EventBroker}.
     */
    protected void sendDownstream(Event evt)
    {
        context.sendDownstream(evt);
    }

    /**
     * Override this method to initialize the handler after scheduler and clock have been set. This method is invoked
     * only once per instance.
     */
    protected void initHandler()
    {
    }

    private Scheduler scheduler;
    private Clock clock;
    private EventHandlerContext context;
}
