package com.gc.mimicry.plugin.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public PortManager()
	{
		allocatedPorts = new HashMap<Integer, Boolean>();
	}

	@Override
	public void handleDownstream( EventHandlerContext ctx, Event evt )
	{
		if ( evt instanceof SocketBindRequestEvent )
		{
			SocketBindRequestEvent bindRequest = (SocketBindRequestEvent) evt;
			handleBindRequest( ctx, bindRequest );
		}
	}

	private boolean isPortAllocated( int port )
	{
		return allocatedPorts.get( port ) != null;
	}

	private boolean isPortReusable( int port )
	{
		return allocatedPorts.get( port );
	}

	private void allocatePort( int port, boolean reusable )
	{
		allocatedPorts.put( port, reusable );
		logger.info( "Allocated port=" + port + ", reusable=" + reusable );
	}

	private int findFreePort( boolean canBeReusable )
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

	private void handleBindRequest( EventHandlerContext ctx, SocketBindRequestEvent bindRequest )
	{
		int port = bindRequest.getEndPoint().getPort();
		boolean reusable = bindRequest.isReusePort();

		if ( port == 0 )
		{
			port = findFreePort( reusable );
		}
		if ( port == -1 )
		{
			// no port available at the moment
			ctx.sendUpstream( new SocketErrorEvent( "No port available at the moment." ) );
			return;
		}

		tryAllocatePort( bindRequest.getDestinationAppId(), bindRequest.getControlFlowId(), ctx, port, reusable );
	}

	private void tryAllocatePort( UUID app, UUID cflow, EventHandlerContext ctx, int port, boolean reusable )
	{
		if ( isPortAllocated( port ) )
		{
			tryReusePort( app, cflow, ctx, port, reusable );
		}
		else
		{
			allocatePort( port, reusable );
			ctx.sendUpstream( new SocketBoundEvent( app, cflow, new InetSocketAddress( port ) ) );
		}
	}

	private void tryReusePort( UUID app, UUID cflow, EventHandlerContext ctx, int port, boolean reusable )
	{
		if ( reusable && isPortReusable( port ) )
		{
			allocatePort( port, REUSABLE );
			ctx.sendUpstream( new SocketBoundEvent( app, cflow, new InetSocketAddress( port ) ) );
		}
		else
		{
			// port already in use
			ctx.sendUpstream( new SocketErrorEvent( "Port " + port + " already in use." ) );
		}
	}

	private static final Logger			logger;
	static
	{
		logger = LoggerFactory.getLogger( PortManager.class );
	}
	private static final int			MIN_PORT	= 1;
	private static final int			MAX_PORT	= 65535;
	private static final boolean		REUSABLE	= true;

	private final Map<Integer, Boolean>	allocatedPorts;
}
