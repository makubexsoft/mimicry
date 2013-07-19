package com.gc.mimicry.plugin.net.udp;

import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.plugin.net.PortManager;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.UDPPacketEvent;

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
	public void handleUpstream( Event evt )
	{
		if ( evt instanceof UDPPacketEvent )
		{
			UDPPacketEvent packetEvt = (UDPPacketEvent) evt;
			Set<UUID> apps = portMgr.getApplicationsOnPort( packetEvt.getDestination().getPort() );
			for ( UUID appId : apps )
			{
				UDPPacketEvent evt2 = new UDPPacketEvent( packetEvt.getSource(), packetEvt.getDestination(),
						packetEvt.getData(), packetEvt.getTimeToLive() );
				evt2.setTargetApp( appId );
				sendUpstream( evt2 );
			}
		}
		else
		{
			sendUpstream( evt );
		}
	}
}
