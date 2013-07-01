package com.gc.mimicry.plugin.tcp;

import java.util.HashMap;
import java.util.Map;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.SocketBindRequestEvent;
import com.gc.mimicry.shared.net.events.SocketBoundEvent;
import com.gc.mimicry.shared.net.events.SocketErrorEvent;

/**
 * Simulates the port management of the operating system.
 * 
 * @author Marc-Christian Schulze
 * 
 * @see SocketBindRequestEvent
 * @see SocketErrorEvent
 * @see SocketBoundEvent
 */
public class PortManager extends EventHandlerBase
{
	private static final int			MIN_PORT	= 1;
	private static final int			MAX_PORT	= 65535;
	private static final boolean		REUSABLE	= true;

	private final Map<Integer, Boolean>	allocatedPorts;

	public PortManager()
	{
		allocatedPorts = new HashMap<Integer, Boolean>();
	}

	public boolean isPortAllocated( int port )
	{
		return allocatedPorts.get( port ) != null;
	}

	public boolean isPortReusable( int port )
	{
		return allocatedPorts.get( port );
	}

	public void allocatePort( int port, boolean reusable )
	{
		allocatedPorts.put( port, reusable );
		System.out.println( "Port [" + port + "] allocated." );
	}

	public int findFreePort( boolean canBeReusable )
	{
		for ( int port = MIN_PORT; port <= MAX_PORT; port++ )
		{
			if ( !isPortAllocated( port ) )
			{
				return port;
			}
			if ( canBeReusable && isPortReusable( port ) )
			{
				return port;
			}
		}
		return -1;
	}

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketBindRequestEvent )
		{
			SocketBindRequestEvent bindRequest = (SocketBindRequestEvent) evt;
			int port = bindRequest.getPort();
			boolean reusable = bindRequest.isReusePort();

			if ( port == 0 )
			{
				port = findFreePort( reusable );
			}
			if ( port == -1 )
			{
				// no port available at the moment
				ctx.sendUpstream( new SocketErrorEvent( "No port available at the moment." ) );
			}

			tryToAllocatePort( ctx, port, reusable );
		}
	}

	private void tryToAllocatePort( EventHandlerContext ctx, int port, boolean reusable )
	{
		if ( isPortAllocated( port ) )
		{
			if ( reusable && isPortReusable( port ) )
			{
				allocatePort( port, REUSABLE );
				ctx.sendUpstream( new SocketBoundEvent( port ) );
			}
			else
			{
				// port already in use
				ctx.sendUpstream( new SocketErrorEvent( "Port " + port + " already in use." ) );
			}
		}
		else
		{
			allocatePort( port, reusable );
			ctx.sendUpstream( new SocketBoundEvent( port ) );
		}
	}
}
