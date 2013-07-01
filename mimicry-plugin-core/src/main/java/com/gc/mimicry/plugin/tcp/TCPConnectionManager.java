package com.gc.mimicry.plugin.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.SocketAcceptedEvent;
import com.gc.mimicry.shared.net.events.SocketAwaitingConnectionEvent;
import com.gc.mimicry.shared.net.events.SocketConnectionRequest;

/**
 * Simulates TCP connection establishment but does not dispatch data of the
 * actual streams.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class TCPConnectionManager extends EventHandlerBase
{
	private final Map<InetSocketAddress, UUID>	localWaitingSockets;

	public TCPConnectionManager()
	{
		localWaitingSockets = new HashMap<InetSocketAddress, UUID>();
	}

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketAwaitingConnectionEvent )
		{
			// local socket awaits connection
			localWaitingSockets.put( ((SocketAwaitingConnectionEvent) evt).getLocalAddress(), evt.getAssociatedControlFlow() );
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
