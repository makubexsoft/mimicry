package com.gc.mimicry.ext.net.events;

import com.gc.mimicry.engine.BaseEvent;

public class SocketErrorEvent extends BaseEvent
{
	private static final long	serialVersionUID	= -721973410083906104L;
	private final String		message;

	public SocketErrorEvent(String msg)
	{
		message = msg;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketErrorEvent [message=" );
		builder.append( message );
		builder.append( "]" );
		return builder.toString();
	}
}
