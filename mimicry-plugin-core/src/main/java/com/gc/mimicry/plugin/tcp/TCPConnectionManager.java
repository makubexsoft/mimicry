package com.gc.mimicry.plugin.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.ConnectionEstablishedEvent;
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
	private final Map<Integer, CFlowRef>	localWaitingSockets;

	public TCPConnectionManager()
	{
		localWaitingSockets = new HashMap<Integer, CFlowRef>();
	}

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketAwaitingConnectionEvent )
		{
			// local socket awaits connection
			// application called ServerSocket#accept()
			CFlowRef ref = new CFlowRef();
			ref.appId = evt.getSourceApplication();
			ref.cflow = evt.getAssociatedControlFlow();

			int port = ((SocketAwaitingConnectionEvent) evt).getLocalAddress().getPort();
			localWaitingSockets.put( port, ref );
		}
		else if ( evt instanceof SocketConnectionRequest )
		{
			// outgoing connection request
			super.handleDownstream( ctx, evt );
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
			int port = ((SocketConnectionRequest) evt).getDestination().getPort();
			CFlowRef ref = localWaitingSockets.remove( port );
			if ( ref != null )
			{
				// bingo - there is a socket listening
				// 1. Inform the application
				ConnectionEstablishedEvent commit = new ConnectionEstablishedEvent(
						((SocketConnectionRequest) evt).getSource(), new InetSocketAddress( port ) );
				commit.setTargetApp( ref.appId );
				commit.setControlFlowId( ref.cflow );
				ctx.sendUpstream( commit );
				// 2. Inform the caller
				ConnectionEstablishedEvent response = new ConnectionEstablishedEvent(
						((SocketConnectionRequest) evt).getSource(), new InetSocketAddress( port ) );
				response.setTargetApp( evt.getSourceApplication() );
				response.setControlFlowId( evt.getAssociatedControlFlow() );
				ctx.sendDownstream( response );
			}
			else
			{
				// sorry - nobody here to pick up the connection
			}
		}
		else if ( evt instanceof ConnectionEstablishedEvent )
		{
			// connection accepted on other side
			super.handleUpstream( ctx, evt );
		}
		else
		{
			super.handleUpstream( ctx, evt );
		}
	}

	private static class CFlowRef
	{
		UUID	appId;
		UUID	cflow;
	}
}
