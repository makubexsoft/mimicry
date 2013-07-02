package com.gc.mimicry.plugin.tcp;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.TCPReceivedDataEvent;
import com.gc.mimicry.shared.net.events.TCPSendDataEvent;

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
		Set<UUID> applications = portMgr.getApplicationOnPort( port );
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