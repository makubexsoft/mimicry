package com.gc.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface TCPPortUnreachable extends Event
{
	public InetSocketAddress getSource();

	public void setSource( InetSocketAddress value );

	public InetSocketAddress getDestination();

	public void setDestination( InetSocketAddress value );
}
