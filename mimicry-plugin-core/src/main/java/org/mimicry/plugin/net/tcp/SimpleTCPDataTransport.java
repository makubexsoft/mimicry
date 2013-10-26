package org.mimicry.plugin.net.tcp;

import java.util.Set;
import java.util.UUID;

import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventHandlerBase;
import org.mimicry.events.net.tcp.TCPReceivedDataEvent;
import org.mimicry.events.net.tcp.TCPSendDataEvent;
import org.mimicry.plugin.net.PortManager;


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
	public void handleUpstream( ApplicationEvent evt )
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

	private void dispatchDataToApplications( TCPSendDataEvent packet )
	{
		// TODO: merge packet clock
		int port = packet.getDestinationSocket().getPort();
		Set<UUID> applications = portMgr.getApplicationsOnPort( port );
		for ( UUID appId : applications )
		{
			TCPReceivedDataEvent event = getEventFactory().createEvent( TCPReceivedDataEvent.class, appId );
			event.setSourceSocket( packet.getSourceSocket() );
			event.setDestinationSocket( packet.getDestinationSocket() );
			event.setData( packet.getData() );
			sendUpstream( event );
		}
	}
}