package org.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.stack.EventHandlerBase;
import org.mimicry.ext.net.tcp.events.ConnectionEstablishedEvent;
import org.mimicry.ext.net.tcp.events.SocketAwaitingConnectionEvent;
import org.mimicry.ext.net.tcp.events.SocketConnectionRequest;


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
	public void handleDownstream( ApplicationEvent evt )
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
	public void handleUpstream( final ApplicationEvent evt )
	{
		if ( evt instanceof SocketConnectionRequest )
		{
			SocketConnectionRequest request = (SocketConnectionRequest) evt;
			handleIncomingConnection( request );
		}
		else if ( evt instanceof TCPPortUnreachable )
		{
			getScheduler().schedule( new Runnable()
			{

				@Override
				public void run()
				{
					SocketConnectionRequest event = getEventFactory().createEvent( SocketConnectionRequest.class,
							evt.getTargetApplication(), evt.getControlFlow(), evt.getApplication() );
					event.setDestination( ((TCPPortUnreachable) evt).getSource() );
					event.setSource( ((TCPPortUnreachable) evt).getDestination() );
					sendDownstream( event );
				}
				// Retry connection attempt every 500 milliseconds
			}, 500, TimeUnit.MILLISECONDS );
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
			TCPPortUnreachable event = getEventFactory().createEvent( TCPPortUnreachable.class,
					request.getTargetApplication(), request.getControlFlow(), request.getApplication() );
			event.setSource( request.getDestination() );
			event.setDestination( request.getSource() );
			sendDownstream( event );
		}
	}

	private void storeSocketInformation( SocketAwaitingConnectionEvent waitEvent )
	{
		CFlowRef ref = new CFlowRef();
		ref.appId = waitEvent.getApplication();
		ref.cflow = waitEvent.getControlFlow();

		int port = waitEvent.getLocalAddress().getPort();
		serverSocketsWaiting.put( port, ref );
	}

	private void informApplication( SocketConnectionRequest request, int port, CFlowRef ref )
	{
		UUID source = request.getApplication();
		ConnectionEstablishedEvent event;
		event = getEventFactory().createEvent( ConnectionEstablishedEvent.class, source, ref.cflow, ref.appId );
		event.setClientAddress( request.getSource() );
		event.setServerAddress( new InetSocketAddress( port ) );
		sendUpstream( event );
	}

	private void informCaller( SocketConnectionRequest request, int port )
	{
		UUID cflow = request.getControlFlow();
		UUID dest = request.getApplication();
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
