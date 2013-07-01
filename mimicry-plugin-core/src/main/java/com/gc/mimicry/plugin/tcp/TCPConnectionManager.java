package com.gc.mimicry.plugin.tcp;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.SocketAcceptedEvent;
import com.gc.mimicry.shared.net.events.SocketAwaitingConnectionEvent;
import com.gc.mimicry.shared.net.events.SocketConnectionRequest;

public class TCPConnectionManager extends EventHandlerBase
{

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketAwaitingConnectionEvent )
		{
			// local socket awaits connection
		}
		else if ( evt instanceof SocketConnectionRequest )
		{
			// outgoing connection request
		}
		else
		{
			super.handleDownstream( ctx, evt );
		}
	}

	@Override
	public void handleUpstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketConnectionRequest )
		{
			// incoming connection
		}
		else if ( evt instanceof SocketAcceptedEvent )
		{
			// connection accepted on other side
		}
		else
		{
			super.handleUpstream( ctx, evt );
		}
	}
}
