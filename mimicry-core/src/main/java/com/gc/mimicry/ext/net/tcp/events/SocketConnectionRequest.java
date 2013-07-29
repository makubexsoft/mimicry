package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.BaseEvent;

public class SocketConnectionRequest extends BaseEvent
{
	private static final long		serialVersionUID	= 2238712155493335986L;
	private final InetSocketAddress	source;
	private final InetSocketAddress	destination;

	public SocketConnectionRequest(InetSocketAddress source, InetSocketAddress destination)
	{
		this.source = source;
		this.destination = destination;
	}

	public InetSocketAddress getSource()
	{
		return source;
	}

	public InetSocketAddress getDestination()
	{
		return destination;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketConnectionRequest [source=" );
		builder.append( source );
		builder.append( ", destination=" );
		builder.append( destination );
		builder.append( "]" );
		return builder.toString();
	}
}
