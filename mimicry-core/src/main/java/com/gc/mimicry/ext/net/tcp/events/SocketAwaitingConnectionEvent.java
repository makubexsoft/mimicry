package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.BaseEvent;
import com.google.common.base.Preconditions;

public class SocketAwaitingConnectionEvent extends BaseEvent
{
	private static final long		serialVersionUID	= -6221348876965913875L;
	private final InetSocketAddress	localAddress;

	public SocketAwaitingConnectionEvent(InetSocketAddress localAddress)
	{
		Preconditions.checkNotNull( localAddress );

		this.localAddress = localAddress;
	}

	public InetSocketAddress getLocalAddress()
	{
		return localAddress;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketAwaitingConnectionEvent [localAddress=" );
		builder.append( localAddress );
		builder.append( "]" );
		return builder.toString();
	}
}
