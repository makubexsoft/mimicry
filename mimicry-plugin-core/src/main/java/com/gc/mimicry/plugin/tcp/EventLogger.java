package com.gc.mimicry.plugin.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.shared.events.Event;

public class EventLogger extends EventHandlerBase
{
	private static final Logger	logger;
	static
	{
		logger = LoggerFactory.getLogger( EventLogger.class );
	}

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		logger.info( "downstream event: " + evt );
		ctx.sendDownstream( evt );
	}

	@Override
	public void handleUpstream( EventHandlerContext ctx, Event evt )
	{
		logger.info( "upstream event: " + evt );
		ctx.sendUpstream( evt );
	}
}
