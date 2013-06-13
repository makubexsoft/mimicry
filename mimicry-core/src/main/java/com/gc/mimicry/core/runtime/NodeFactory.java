package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.NodeDescriptor;
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

	public NodeFactory(ClassLoadingContext ctx, EventHandlerFactory handlerFactory, MessagingSystem messaging)
	{
		Preconditions.checkNotNull( ctx );
		Preconditions.checkNotNull( handlerFactory );
		Preconditions.checkNotNull( messaging );

		this.ctx = ctx;
		this.handlerFactory = handlerFactory;
		this.messaging = messaging;
	}

	public Node createNode( NodeDescriptor descriptor )
	{
		Preconditions.checkNotNull( descriptor );

		Node node = new Node( ctx, descriptor.getNodeName(), messaging );

		ClockDriver clockDriver = new ClockDriver( messaging, node );
		node.attachResource( clockDriver );

		EventStack eventStack = node.getEventStack();
		for ( String handlerClassName : descriptor.getEventStack() )
		{
			EventHandler handler = handlerFactory.create( handlerClassName, ctx.getCoreClassLoader() );
			if ( handler != null )
			{
				// TODO: init handler???
				eventStack.addHandler( handler );
			}
		}
		return node;
	}
}
