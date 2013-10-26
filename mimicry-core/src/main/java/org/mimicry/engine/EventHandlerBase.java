package org.mimicry.engine;

import org.mimicry.cep.CEPEngine;
import org.mimicry.timing.Scheduler;
import org.mimicry.timing.Timeline;

import com.google.common.base.Preconditions;

/**
 * Base class for most of the {@link EventHandler}s.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventHandlerBase implements EventHandler
{
    protected EventHandlerBase()
    {
        identity = Identity.create(getClass().getSimpleName());
        eventFactory = DefaultEventFactory.create(getIdentity());
    }

    @Override
    final public void init(EventHandlerContext ctx, Scheduler scheduler, Timeline clock, CEPEngine eventEngine)
    {
        Preconditions.checkNotNull(ctx);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(clock);
        Preconditions.checkNotNull(eventEngine);

        context = ctx;
        this.scheduler = scheduler;
        this.clock = clock;
        this.eventEngine = eventEngine;

        initHandler();
    }

    @Override
    final public Scheduler getScheduler()
    {
        return scheduler;
    }

    final public Timeline getClock()
    {
        return clock;
    }

    final public EventHandlerContext getContext()
    {
        return context;
    }

    final public CEPEngine getEventEngine()
    {
        return eventEngine;
    }

    /**
     * Override to handle events passed down the {@link EventStack}.
     * 
     * @param evt
     */
    @Override
    public void handleDownstream(ApplicationEvent evt)
    {
        context.sendDownstream(evt);
    }

    /**
     * Override to handle events passed up in the {@link EventStack}.
     * 
     * @param evt
     */
    @Override
    public void handleUpstream(ApplicationEvent evt)
    {
        context.sendUpstream(evt);
    }

    /**
     * Send the given event to the next event handler upstream in the {@link EventStack}. Once the top of the
     * {@link EventStack} is reached the event is dispatched to the application identified by the id within the
     * {@link ApplicationEvent#getTargetApplication()} attribute. If no such application exists the event is dropped. If
     * you override this method make sure that you pass all events not of your interest upstream. Otherwise you would
     * suppress the event.
     * 
     * @param evt
     *            The event received either from an {@link EventHandler} higher in the {@link EventStack} or one of the
     *            {@link LocalJVMApplication} running this {@link LocalNode}.
     */
    protected void sendUpstream(ApplicationEvent evt)
    {
        context.sendUpstream(evt);
    }

    /**
     * Send the given event to the next event handler downstream in the {@link EventStack}. Once the bottom of the
     * {@link EventStack} is reached the event is dispatched using the {@link EventEngine} to the event stacks of all
     * other nodes.If you override this method make sure that you pass all events not of your interest downstream.
     * Otherwise you would suppress the event.
     * 
     * @param evt
     *            The event received either from an {@link EventHandler} lower in the {@link EventStack} or the
     *            {@link EventEngine}.
     */
    protected void sendDownstream(ApplicationEvent evt)
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

    @Override
    public Identity getIdentity()
    {
        return identity;
    }

    @Override
    public EventFactory getEventFactory()
    {
        return eventFactory;
    }

    private final EventFactory eventFactory;
    private final Identity identity;
    private Scheduler scheduler;
    private Timeline clock;
    private EventHandlerContext context;
    private CEPEngine eventEngine;
}
