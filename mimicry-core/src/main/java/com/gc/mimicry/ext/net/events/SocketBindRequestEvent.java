package com.gc.mimicry.ext.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.BaseEvent;

public class SocketBindRequestEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 1L;
	private final InetSocketAddress	endpoint;
	private final SocketType		socketType;
	private final boolean			reusePort;

	public SocketBindRequestEvent(InetSocketAddress endpoint, SocketType socketType, boolean reusePort)
	{
		this.endpoint = endpoint;
		this.socketType = socketType;
		this.reusePort = reusePort;
	}

	public InetSocketAddress getEndPoint()
	{
		return endpoint;
	}

	public boolean isReusePort()
	{
		return reusePort;
	}

	public SocketType getSocketType()
	{
		return socketType;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketBindRequestEvent [endpoint=" );
		builder.append( endpoint );
		builder.append( ", socketType=" );
		builder.append( socketType );
		builder.append( ", reusePort=" );
		builder.append( reusePort );
		builder.append( "]" );
		return builder.toString();
	}
}
