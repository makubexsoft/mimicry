package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketConnectionRequest extends BaseEvent
{
	private InetSocketAddress	source;
	private InetSocketAddress	destination;

	public SocketConnectionRequest(UUID appId, UUID controlFlowId, InetSocketAddress source,
			InetSocketAddress destination)
	{
		super( appId, controlFlowId );

		this.source = source;
		this.destination = destination;
	}

}
