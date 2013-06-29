package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.core.event.EventHandler;
import com.gc.mimicry.core.event.EventHandlerFactory;
import com.gc.mimicry.core.event.EventStack;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockBasedScheduler;
import com.google.common.base.Preconditions;

/**
 * A node factory is able to create new nodes that run their applications in the local JVM.
 * 
 * @author Marc-Christian Schulze
 *
 */
public class NodeFactory
{
	private final EventHandlerFactory	handlerFactory;
	private final EventBroker eventBroker;
	private final ClassLoadingContext	ctx;
	private Clock clock;

	public NodeFactory(ClassLoadingContext ctx, EventHandlerFactory handlerFactory, EventBroker eventBroker, Clock clock)
	{
		Preconditions.checkNotNull( ctx );
		Preconditions.checkNotNull( handlerFactory );
		Preconditions.checkNotNull( eventBroker );
		Preconditions.checkNotNull(clock);

		this.ctx = ctx;
		this.handlerFactory = handlerFactory;
		this.eventBroker = eventBroker;
		this.clock = clock;
	}

	public Node createNode( NodeConfiguration descriptor )
	{
		Preconditions.checkNotNull( descriptor );

		Node node = new Node( ctx, descriptor.getNodeName(), eventBroker, clock );

		// TODO:
//		ClockDriver clockDriver = new ClockDriver( messaging, node );
//		node.attachResource( clockDriver );

		EventStack eventStack = node.getEventStack();
		for ( EventHandlerConfiguration handlerConfig : descriptor.getEventStack() )
		{
			EventHandler handler = handlerFactory.create( handlerConfig.getClassName(), ctx.getCoreClassLoader() );
			if ( handler != null )
			{
				if(handler instanceof Configurable)
				{
					Configurable configurable = (Configurable)handler;
					configurable.configure( handlerConfig.getConfiguration() );
				}
				
				// Jobs of a single event handler are executed in a single thread.
				// Event passing to the handler should be done through the scheduler
				// by doing so the event handler is not forced to be concerned about multi-threading issues.
				handler.init(new ClockBasedScheduler(clock), clock);
				eventStack.addHandler( handler );
			}
		}
		return node;
	}
}
