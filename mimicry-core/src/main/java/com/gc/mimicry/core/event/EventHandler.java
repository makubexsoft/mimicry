package com.gc.mimicry.core.event;

import com.gc.mimicry.core.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;

/**
 * Event handlers can be attached to {@link EventStack}s of a logical node. The
 * handlers are responsible to handle events emitted by the simulated
 * application. Implementations are required to provide a constructor with the
 * signature <br/>
 * <code>ctor({@link Scheduler} scheduler, {@link Clock}
 * clock, {@link SessionLocalMessagingService} messaging)</code><br/>
 * Event handlers are not allowed to spawn threads nor are they allowed to block
 * the control flow in its handler methods. If the handler requires to delay the
 * event forwarding it can use the {@link Scheduler} passed in the constructor.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface EventHandler
{

	public void init( Scheduler scheduler, Clock clock, SessionLocalMessagingService messaging );

	public void setClock( Clock clock );

	/**
	 * Gets invoked when an event is passed down in the {@link EventStack} which
	 * means it's an outgoing event of the application. This method must not
	 * block. If you need to delay the event forwarding use the
	 * {@link Scheduler} passed in the constructor. To pass the given event
	 * further down or up you can use the
	 * {@link EventHandlerContext#sendDownstream(Event)} and
	 * {@link EventHandlerContext#sendUpstream(Event)} methods.
	 * 
	 * @param ctx
	 *            The event context which provides access to the
	 *            {@link EventStack}.
	 * @param evt
	 *            The event passed downstream.
	 */
	public void handleDownstream( EventHandlerContext ctx, Event evt );

	/**
	 * Gets invoked when an event is passed up in the {@link EventStack} which
	 * means it's an incoming event to the application. This method must not
	 * block. If you need to delay the event forwarding use the
	 * {@link Scheduler} passed in the constructor. To pass the given event
	 * further up or down you can use the
	 * {@link EventHandlerContext#sendUpstream(Event)} and
	 * {@link EventHandlerContext#sendDownstream(Event)} methods.
	 * 
	 * @param ctx
	 *            The event context which provides access to the
	 *            {@link EventStack}.
	 * @param evt
	 *            The event passed upstream.
	 */
	public void handleUpstream( EventHandlerContext ctx, Event evt );
}
