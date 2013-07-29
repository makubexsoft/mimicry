package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.BaseEvent;

/**
 * This event indicates a newly established TCP/IP connection.
 * 
 * @author Marc-Christian Schulze
 * 
 */
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

	/**
	 * Returns the address of the peer that initiated the connection.
	 * 
	 * @return
	 */
	public InetSocketAddress getClientAddress()
	{
		return clientAddress;
	}

	/**
	 * Returns the address of the peer that accepted the incoming connection.
	 * 
	 * @return
	 */
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
