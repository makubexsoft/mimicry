package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;
import com.gc.mimicry.shared.events.Event;

public class SocketAwaitingConnectionEvent extends BaseEvent
{

	public SocketAwaitingConnectionEvent(UUID appId, UUID controlFlowId)
	{
		super( appId, controlFlowId );
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketAwaitingConnectionEvent []" );
		return builder.toString();
	}
	
}
