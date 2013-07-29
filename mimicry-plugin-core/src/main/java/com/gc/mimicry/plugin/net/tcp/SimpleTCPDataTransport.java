package com.gc.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.stack.EventHandlerBase;
import com.gc.mimicry.ext.net.tcp.events.TCPReceivedDataEvent;
import com.gc.mimicry.ext.net.tcp.events.TCPSendDataEvent;
import com.gc.mimicry.plugin.net.PortManager;

/**
 * This handler imitates a simple TCP/IP packet transport.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class SimpleTCPDataTransport extends EventHandlerBase
{
	private PortManager	portMgr;

	@Override
	protected void initHandler()
	{
		portMgr = getContext().findHandler( PortManager.class );
		if ( portMgr == null )
		{
			throw new RuntimeException( "This implementation requires the " + PortManager.class
					+ " to be within the event stack." );
		}
	}

	@Override
	public void handleUpstream( Event evt )
	{
		if ( evt instanceof TCPSendDataEvent )
		{
			TCPSendDataEvent dataEvent = (TCPSendDataEvent) evt;
			dispatchDataToApplications( dataEvent );
		}
		else
		{
			sendUpstream( evt );
		}
	}

	private void dispatchDataToApplications( TCPSendDataEvent dataEvent )
	{
		int port = dataEvent.getDestinationSocket().getPort();
		Set<UUID> applications = portMgr.getApplicationsOnPort( port );
		for ( UUID appId : applications )
		{
			TCPReceivedDataEvent receiveEvt = createReceiveEvent( dataEvent );
			receiveEvt.setTargetApp( appId );

			sendUpstream( receiveEvt );
		}
	}

	private TCPReceivedDataEvent createReceiveEvent( TCPSendDataEvent dataEvent )
	{
		InetSocketAddress source = dataEvent.getSourceSocket();
		InetSocketAddress destination = dataEvent.getDestinationSocket();
		return new TCPReceivedDataEvent( source, destination, dataEvent.getData() );
	}
}
