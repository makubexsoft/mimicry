package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;
import com.google.common.base.Preconditions;

public class SetServerSocketOptionEvent extends BaseEvent
{
	private static final long			serialVersionUID	= 2424920756699509261L;
	private final InetSocketAddress		socketAddress;
	private final ServerSocketOption	option;
	private int							intValue;
	private boolean						boolValue;

	public SetServerSocketOptionEvent(InetSocketAddress socketAddress, ServerSocketOption option, int value)
	{
		Preconditions.checkNotNull( option );

		this.socketAddress = socketAddress;
		this.option = option;
		intValue = value;
	}

	public SetServerSocketOptionEvent(InetSocketAddress socketAddress, ServerSocketOption option, boolean value)
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

	public ServerSocketOption getOption()
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
}
