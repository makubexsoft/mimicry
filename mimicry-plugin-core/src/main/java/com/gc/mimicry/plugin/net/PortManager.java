package com.gc.mimicry.plugin.net;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.event.ApplicationEvent;
import com.gc.mimicry.engine.stack.EventHandlerBase;
import com.gc.mimicry.ext.net.events.SocketBindRequestEvent;
import com.gc.mimicry.ext.net.events.SocketBoundEvent;
import com.gc.mimicry.ext.net.events.SocketErrorEvent;

/**
 * Simulates the port management of the operating system for UDP and TCP
 * sockets.
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
		allocatedPorts = new HashMap<Integer, PortInfo>();
	}

	@Override
	public void handleDownstream( ApplicationEvent evt )
	{
		if ( evt instanceof SocketBindRequestEvent )
		{
			SocketBindRequestEvent bindRequest = (SocketBindRequestEvent) evt;
			handleBindRequest( bindRequest );
		}
		else
		{
			sendDownstream( evt );
		}
	}

	private void handleBindRequest( SocketBindRequestEvent bindRequest )
	{
		int port = bindRequest.getEndPoint().getPort();
		boolean reusable = bindRequest.isReusePort();
		UUID cflow = bindRequest.getControlFlow();
		UUID dest = bindRequest.getApplication();

		if ( port == 0 )
		{
			port = findFreePort( reusable );
		}

		if ( port == -1 )
		{
			SocketErrorEvent event;
			event = getEventFactory().createEvent( SocketErrorEvent.class, null, cflow, dest );
			event.setMessage( "No port available at the moment." );
			sendUpstream( event );

			return;
		}

		tryAllocatePort( dest, cflow, port, reusable );
	}

	private void tryAllocatePort( UUID app, UUID cflow, int port, boolean reusable )
	{
		if ( isPortAllocated( port ) )
		{
			tryReusePort( app, cflow, port, reusable );
		}
		else
		{
			allocatePort( app, port, reusable );

			SocketBoundEvent event;
			event = getEventFactory().createEvent( SocketBoundEvent.class, null, cflow, app );
			event.setAddress( new InetSocketAddress( port ) );
			sendUpstream( event );
		}
	}

	private void tryReusePort( UUID app, UUID cflow, int port, boolean reusable )
	{
		if ( reusable && isPortReusable( port ) )
		{
			allocatePort( app, port, REUSABLE );

			SocketBoundEvent event;
			event = getEventFactory().createEvent( SocketBoundEvent.class, null, cflow, app );
			event.setAddress( new InetSocketAddress( port ) );
			sendUpstream( event );
		}
		else
		{
			// port already in use
			SocketErrorEvent event = getEventFactory().createEvent( SocketErrorEvent.class, null, cflow, app );
			event.setMessage( "Port " + port + " already in use." );
			sendUpstream( event );
		}
	}

	/**
	 * Returns whether the given port has been allocated by any application.
	 * 
	 * @param port
	 * @return
	 */
	public boolean isPortAllocated( int port )
	{
		PortInfo portInfo = allocatedPorts.get( port );
		if ( portInfo == null )
		{
			return false;
		}
		return portInfo.applications.size() > 0;
	}

	/**
	 * Returns whether the given port is reusable by sockets having the
	 * SO_REUSEADDR option enabled.
	 * 
	 * @param port
	 * @return
	 */
	public boolean isPortReusable( int port )
	{
		if ( !isPortAllocated( port ) )
		{
			return true;
		}
		return allocatedPorts.get( port ).reusable;
	}

	/**
	 * Returns a set of ids of the applications which allocated the given port;
	 * or an empty set if no at all.
	 * 
	 * @param port
	 * @return
	 */
	public Set<UUID> getApplicationsOnPort( int port )
	{
		PortInfo portInfo = allocatedPorts.get( port );
		if ( portInfo != null )
		{
			return portInfo.applications;
		}
		return Collections.emptySet();
	}

	private void allocatePort( UUID appId, int port, boolean reusable )
	{
		PortInfo portInfo = allocatedPorts.get( port );
		if ( portInfo == null )
		{
			portInfo = new PortInfo( appId, reusable );
			allocatedPorts.put( port, portInfo );
		}
		else
		{
			portInfo.applications.add( appId );
		}
		logger.info( "Allocated port=" + port + ", reusable=" + reusable + ", app=" + appId );
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

	static
	{
		logger = LoggerFactory.getLogger( PortManager.class );
	}
	private static final Logger				logger;
	private static final int				MIN_PORT	= 1;
	private static final int				MAX_PORT	= 65535;
	private static final boolean			REUSABLE	= true;
	private final Map<Integer, PortInfo>	allocatedPorts;

	private static final class PortInfo
	{
		public PortInfo(UUID appId, boolean reusable)
		{
			applications.add( appId );
			this.reusable = reusable;
		}

		public boolean		reusable;
		public Set<UUID>	applications	= new HashSet<UUID>();
	}
}
