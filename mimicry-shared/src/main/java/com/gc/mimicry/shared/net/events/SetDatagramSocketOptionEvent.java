package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetDatagramSocketOptionEvent extends BaseEvent
{
	private static final long			serialVersionUID	= 3357391817299136386L;
	private final InetSocketAddress		socketAddres;
	private final DatagramSocketOption	option;
	private int							intValue;
	private boolean						boolValue;

	public SetDatagramSocketOptionEvent(InetSocketAddress socketAddres, DatagramSocketOption option, int intValue)
	{
		this.socketAddres = socketAddres;
		this.option = option;
		this.intValue = intValue;
	}

	public SetDatagramSocketOptionEvent(InetSocketAddress socketAddres, DatagramSocketOption option, boolean boolValue)
	{
		this.socketAddres = socketAddres;
		this.option = option;
		this.boolValue = boolValue;
	}

	public InetSocketAddress getSocketAddres()
	{
		return socketAddres;
	}

	public DatagramSocketOption getOption()
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
		builder.append( "SetDatagramSocketOptionEvent [socketAddres=" );
		builder.append( socketAddres );
		builder.append( ", option=" );
		builder.append( option );
		builder.append( ", intValue=" );
		builder.append( intValue );
		builder.append( ", boolValue=" );
		builder.append( boolValue );
		builder.append( "]" );
		return builder.toString();
	}
}
