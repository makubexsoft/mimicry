package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.event.EventHandler;
import com.gc.mimicry.core.event.EventHandlerFactory;
import com.gc.mimicry.core.event.EventStack;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.timing.net.ClockDriver;
import com.google.common.base.Preconditions;

public class NodeFactory
{

	private final EventHandlerFactory	handlerFactory;
	private final MessagingSystem		messaging;
	private final ClassLoadingContext	ctx;

	// TODO: there should be no dep. to the msg. system
	public NodeFactory(ClassLoadingContext ctx, EventHandlerFactory handlerFactory, MessagingSystem messaging)
	{
		Preconditions.checkNotNull( ctx );
		Preconditions.checkNotNull( handlerFactory );
		Preconditions.checkNotNull( messaging );

		this.ctx = ctx;
		this.handlerFactory = handlerFactory;
		this.messaging = messaging;
	}

	public Node createNode( NodeConfiguration descriptor )
	{
		Preconditions.checkNotNull( descriptor );

		Node node = new Node( ctx, descriptor.getNodeName(), messaging );

		ClockDriver clockDriver = new ClockDriver( messaging, node );
		node.attachResource( clockDriver );

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
				
				// TODO: init handler???
				eventStack.addHandler( handler );
			}
		}
		return node;
	}
}
