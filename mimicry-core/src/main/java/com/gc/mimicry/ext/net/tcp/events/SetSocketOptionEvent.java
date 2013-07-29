package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.BaseEvent;
import com.google.common.base.Preconditions;

public class SetSocketOptionEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 421837596215880294L;
	private final InetSocketAddress	socketAddress;
	private final SocketOption		option;
	private int						intValue;
	private boolean					boolValue;

	public SetSocketOptionEvent(InetSocketAddress socketAddress, SocketOption option, int value)
	{
		Preconditions.checkNotNull( option );

		this.socketAddress = socketAddress;
		this.option = option;
		intValue = value;
	}

	public SetSocketOptionEvent(InetSocketAddress socketAddress, SocketOption option, int intValue, boolean boolValue)
	{
		Preconditions.checkNotNull( option );

		this.socketAddress = socketAddress;
		this.option = option;
		this.intValue = intValue;
		this.boolValue = boolValue;
	}

	public SetSocketOptionEvent(InetSocketAddress socketAddress, SocketOption option, boolean value)
	{
		Preconditions.checkNotNull( option );

		this.socketAddress = socketAddress;
		this.option = option;
		boolValue = value;
	}

	public InetSocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	public SocketOption getOption()
	{
		return option;
	}

	public int getIntValue()
	{
		return intValue;
	}

	public boolean isBoolValue()
	{
		return boolValue;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SetSocketOptionEvent [option=" );
		builder.append( option );
		builder.append( ", intValue=" );
		builder.append( intValue );
		builder.append( ", boolValue=" );
		builder.append( boolValue );
		builder.append( "]" );
		return builder.toString();
	}

}
