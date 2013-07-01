package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetSocketOptionEvent extends BaseEvent
{
	private static final long	serialVersionUID	= 421837596215880294L;
	private SocketOption option;
	private int intValue;
	private boolean boolValue;

	public SetSocketOptionEvent(UUID appId,  SocketOption option, int value)
	{
		super( appId );
		this.option = option;
		intValue = value;
	}
	public SetSocketOptionEvent(UUID appId,  SocketOption option, int intValue, boolean boolValue)
	{
		super( appId );
		this.option = option;
		this.intValue = intValue;
		this.boolValue = boolValue;
	}
	
	public SetSocketOptionEvent(UUID appId,  SocketOption option, boolean value)
	{
		super( appId );
		this.option = option;
		boolValue = value;
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
}
