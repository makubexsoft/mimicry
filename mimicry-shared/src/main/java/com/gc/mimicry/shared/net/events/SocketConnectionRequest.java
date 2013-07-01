package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketConnectionRequest extends BaseEvent
{
	private static final long	serialVersionUID	= 2238712155493335986L;
	private InetSocketAddress	source;
	private InetSocketAddress	destination;

	public SocketConnectionRequest(UUID appId, UUID controlFlowId, InetSocketAddress source,
			InetSocketAddress destination)
	{
		super( appId, controlFlowId );

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
		builder.append( ", toString()=" );
		builder.append( super.toString() );
		builder.append( "]" );
		return builder.toString();
	}
}
