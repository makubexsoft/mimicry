package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetMulticastSocketOptionEvent extends BaseEvent
{
	private static final long			serialVersionUID	= 8856624267641794948L;
	private final InetSocketAddress		socketAddress;
	private final MulticastSocketOption	option;
	private final Object				value;

	public SetMulticastSocketOptionEvent(InetSocketAddress socketAddress, MulticastSocketOption option, Object value)
	{
		this.socketAddress = socketAddress;
		this.option = option;
		this.value = value;
	}

	public InetSocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	public MulticastSocketOption getOption()
	{
		return option;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SetMulticastSocketOptionEvent [socketAddress=" );
		builder.append( socketAddress );
		builder.append( ", option=" );
		builder.append( option );
		builder.append( ", value=" );
		builder.append( value );
		builder.append( "]" );
		return builder.toString();
	}
}
