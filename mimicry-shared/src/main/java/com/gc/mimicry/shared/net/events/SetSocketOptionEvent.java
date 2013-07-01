package com.gc.mimicry.shared.net.events;

import com.gc.mimicry.shared.events.BaseEvent;
import com.google.common.base.Preconditions;

public class SetSocketOptionEvent extends BaseEvent
{
	private static final long	serialVersionUID	= 421837596215880294L;
	private final SocketOption	option;
	private int					intValue;
	private boolean				boolValue;

	public SetSocketOptionEvent(SocketOption option, int value)
	{
		Preconditions.checkNotNull( option );

		this.option = option;
		intValue = value;
	}

	public SetSocketOptionEvent(SocketOption option, int intValue, boolean boolValue)
	{
		Preconditions.checkNotNull( option );

		this.option = option;
		this.intValue = intValue;
		this.boolValue = boolValue;
	}

	public SetSocketOptionEvent(SocketOption option, boolean value)
	{
		Preconditions.checkNotNull( option );

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
