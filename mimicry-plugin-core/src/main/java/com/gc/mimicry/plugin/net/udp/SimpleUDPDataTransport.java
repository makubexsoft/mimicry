package com.gc.mimicry.plugin.net.udp;

import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.EventHandlerBase;
import com.gc.mimicry.ext.net.udp.events.UDPPacketEvent;
import com.gc.mimicry.plugin.net.PortManager;

/**
 * This handler imitates a simple and reliable UDP/IP packet transport.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class SimpleUDPDataTransport extends EventHandlerBase
{
	private PortManager	portMgr;

	@Override
	protected void initHandler()
	{
		portMgr = getContext().findHandler( PortManager.class );
		if ( portMgr == null )
		{
			throw new RuntimeException( "This handler can only be used in conjunction with the PortManager." );
		}
	}

	@Override
	public void handleUpstream( Event event )
	{
		if ( event instanceof UDPPacketEvent )
		{
			UDPPacketEvent packet = (UDPPacketEvent) event;
			handlePacket( packet );
		}
		else
		{
			sendUpstream( event );
		}
	}

	private void handlePacket( UDPPacketEvent packet )
	{
		// TODO: merge packet clock
		Set<UUID> apps = portMgr.getApplicationsOnPort( packet.getDestination().getPort() );
		for ( UUID appId : apps )
		{
			UDPPacketEvent event = getEventFactory().createEvent( UDPPacketEvent.class, appId );
			event.setSource( packet.getSource() );
			event.setDestination( packet.getDestination() );
			event.setData( packet.getData() );
			event.setTimeToLive( packet.getTimeToLive() );
			sendUpstream( event );
		}
	}
}
