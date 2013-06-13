package com.gc.mimicry.core.event;

import com.gc.mimicry.core.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;

public class EventAdapter implements EventHandler
{

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		ctx.sendDownstream( evt );
	}

	@Override
	public void handleUpstream( EventHandlerContext ctx, Event evt )
	{
		ctx.sendUpstream( evt );
	}

	@Override
	public void init( Scheduler scheduler, Clock clock, SessionLocalMessagingService messaging )
	{
	}

	@Override
	public void setClock( Clock clock )
	{
	}
}
