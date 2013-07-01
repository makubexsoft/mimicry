package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;

public class ConnectionEstablishedEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 5351265649818558044L;
	private final InetSocketAddress	clientAddress;
	private final InetSocketAddress	serverAddress;

	public ConnectionEstablishedEvent(InetSocketAddress clientAddress, InetSocketAddress serverAddress)
	{
		this.clientAddress = clientAddress;
		this.serverAddress = serverAddress;
	}

	public InetSocketAddress getClientAddress()
	{
		return clientAddress;
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "ConnectionEstablishedEvent [clientAddress=" );
		builder.append( clientAddress );
		builder.append( ", serverAddress=" );
		builder.append( serverAddress );
		builder.append( "]" );
		return builder.toString();
	}
}
