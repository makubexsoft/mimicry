package com.gc.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.EventHandlerBase;
import com.gc.mimicry.ext.net.tcp.events.ConnectionEstablishedEvent;
import com.gc.mimicry.ext.net.tcp.events.SocketAwaitingConnectionEvent;
import com.gc.mimicry.ext.net.tcp.events.SocketConnectionRequest;

/**
 * Simulates TCP connection establishment but does not dispatch data of the
 * actual streams.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class TCPConnectionManager extends EventHandlerBase
{
	private final Map<Integer, CFlowRef>	serverSocketsWaiting;

	public TCPConnectionManager()
	{
		serverSocketsWaiting = new HashMap<Integer, CFlowRef>();
	}

	@Override
	public void handleDownstream( Event evt )
	{
		if ( evt instanceof SocketAwaitingConnectionEvent )
		{
			SocketAwaitingConnectionEvent waitEvent = (SocketAwaitingConnectionEvent) evt;

			// local socket awaits connection
			// application called ServerSocket#accept()
			storeSocketInformation( waitEvent );
		}
		else
		{
			sendDownstream( evt );
		}
	}

	@Override
	public void handleUpstream( Event evt )
	{
		if ( evt instanceof SocketConnectionRequest )
		{
			SocketConnectionRequest request = (SocketConnectionRequest) evt;
			handleIncomingConnection( request );
		}
		else
		{
			sendUpstream( evt );
		}
	}

	private void handleIncomingConnection( SocketConnectionRequest request )
	{
		int port = request.getDestination().getPort();
		CFlowRef ref = serverSocketsWaiting.remove( port );
		if ( ref != null )
		{
			// bingo - there is a socket listening
			informApplication( request, port, ref );
			informCaller( request, port );
		}
		else
		{
			// sorry - nobody here to pick up the connection
		}
	}

	private void storeSocketInformation( SocketAwaitingConnectionEvent waitEvent )
	{
		CFlowRef ref = new CFlowRef();
		ref.appId = waitEvent.getSourceApplication();
		ref.cflow = waitEvent.getAssociatedControlFlow();

		int port = waitEvent.getLocalAddress().getPort();
		serverSocketsWaiting.put( port, ref );
	}

	private void informApplication( SocketConnectionRequest request, int port, CFlowRef ref )
	{
		UUID source = request.getSourceApplication();
		ConnectionEstablishedEvent event;
		event = getEventFactory().createEvent( ConnectionEstablishedEvent.class, source, ref.cflow, ref.appId );
		event.setClientAddress( request.getSource() );
		event.setServerAddress( new InetSocketAddress( port ) );
		sendUpstream( event );
	}

	private void informCaller( SocketConnectionRequest request, int port )
	{
		UUID cflow = request.getAssociatedControlFlow();
		UUID dest = request.getSourceApplication();
		ConnectionEstablishedEvent event;
		event = getEventFactory().createEvent( ConnectionEstablishedEvent.class, null, cflow, dest );
		sendDownstream( event );
	}

	private static class CFlowRef
	{
		UUID	appId;
		UUID	cflow;
	}
}
