package org.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;

import org.mimicry.engine.event.ApplicationEvent;


public interface TCPPortUnreachable extends ApplicationEvent
{
	public InetSocketAddress getSource();

	public void setSource( InetSocketAddress value );

	public InetSocketAddress getDestination();

	public void setDestination( InetSocketAddress value );
}
