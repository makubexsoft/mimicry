package com.gc.mimicry.shared.net.events;

import com.gc.mimicry.shared.events.BaseEvent;

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
