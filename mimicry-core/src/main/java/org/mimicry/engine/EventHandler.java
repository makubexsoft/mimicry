package org.mimicry.engine;

import org.mimicry.cep.CEPEngine;
import org.mimicry.timing.Scheduler;
import org.mimicry.timing.Timeline;

/**
 * An {@link EventHandler} is part of an {@link EventStack} attached to a {@link LocalNode}. It's highly recommended not
 * to create any threads within an {@link EventHandler}, instead use the given {@link Scheduler}. As long as the event
 * handler is using only the given {@link Scheduler} instance for performing asynchronous tasks it has not to consider
 * any thread synchronisation. By default all methods invoked on this event handler are performed in a dedicated thread
 * (the "Event Handler Thread" - EHT) to this handler and therefore thread-safe. This also applies for jobs being
 * executed by the given scheduler.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface EventHandler
{
    /**
     * Initializes this handler instance after it has been created and before being attached to the {@link EventStack}.
     * 
     * @param scheduler
     *            Use this scheduler for all asynchronous operations required by this handler. The scheduler will use
     *            the EHT to run the scheduled jobs which makes each event handler fully thread-safe.
     * @param clock
     *            A clock to obtain the current time of the simulation. Note that this clock is not necessarily
     *            synchronized with the real-time.
     */
    public void init(EventHandlerContext ctx, Scheduler scheduler, Timeline clock, CEPEngine eventEngine);

    public Scheduler getScheduler();

    /**
     * Gets invoked when an event is passed down in the {@link EventStack} which means it's an outgoing event of the
     * application. This method must not block. If you need to delay the event forwarding use the {@link Scheduler}
     * passed in the constructor. To pass the given event further down or up you can use the
     * {@link EventHandlerContext#sendDownstream(ApplicationEvent)} and
     * {@link EventHandlerContext#sendUpstream(ApplicationEvent)} methods. This method is only invoked from within the
     * EHT.
     * 
     * @param evt
     *            The event passed downstream.
     */
    public void handleDownstream(ApplicationEvent evt);

    /**
     * Gets invoked when an event is passed up in the {@link EventStack} which means it's an incoming event to the
     * application. This method must not block. If you need to delay the event forwarding use the {@link Scheduler}
     * passed in the constructor. To pass the given event further up or down you can use the
     * {@link EventHandlerContext#sendUpstream(ApplicationEvent)} and
     * {@link EventHandlerContext#sendDownstream(ApplicationEvent)} methods. This method is only invoked from within the
     * EHT.
     * 
     * @param evt
     *            The event passed upstream.
     */
    public void handleUpstream(ApplicationEvent evt);

    public Identity getIdentity();

    public EventFactory getEventFactory();
}
