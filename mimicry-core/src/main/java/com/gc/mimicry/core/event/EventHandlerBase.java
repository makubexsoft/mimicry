package com.gc.mimicry.core.event;

import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;
import com.gc.mimicry.shared.events.Event;
import com.google.common.base.Preconditions;

public class EventHandlerBase implements EventHandler
{

    @Override
    public void handleDownstream(EventHandlerContext ctx, Event evt)
    {
        ctx.sendDownstream(evt);
    }

    @Override
    public void handleUpstream(EventHandlerContext ctx, Event evt)
    {
        ctx.sendUpstream(evt);
    }

    /**
     * Override this method to initialize the handler after scheduler and clock have been set.
     */
    protected void initHandler()
    {
    }

    @Override
    final public void init(Scheduler scheduler, Clock clock)
    {
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(clock);

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

    private Scheduler scheduler;
    private Clock clock;
}
