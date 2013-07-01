package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;
import com.google.common.base.Preconditions;

public class SocketBoundEvent extends BaseEvent
{
	private static final long		serialVersionUID	= -696695113477236492L;
	private final InetSocketAddress	address;

	public SocketBoundEvent(InetSocketAddress address)
	{
		Preconditions.checkNotNull( address );

		this.address = address;
	}

	public InetSocketAddress getAddress()
	{
		return address;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketBoundEvent [address=" );
		builder.append( address );
		builder.append( "]" );
		return builder.toString();
	}

}
