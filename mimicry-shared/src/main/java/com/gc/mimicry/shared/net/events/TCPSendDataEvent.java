package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.gc.mimicry.shared.events.BaseEvent;

public class TCPSendDataEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 5716616721733824001L;
	private final InetSocketAddress	sourceSocket;
	private final InetSocketAddress	destinationSocket;
	private final byte[]			data;

	public TCPSendDataEvent(InetSocketAddress sourceSocket, InetSocketAddress destinationSocket, byte[] data)
	{
		super();
		this.sourceSocket = sourceSocket;
		this.destinationSocket = destinationSocket;
		this.data = data;
	}

	public InetSocketAddress getSourceSocket()
	{
		return sourceSocket;
	}

	public InetSocketAddress getDestinationSocket()
	{
		return destinationSocket;
	}

	public byte[] getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "TCPSendDataEvent [sourceSocket=" );
		builder.append( sourceSocket );
		builder.append( ", destinationSocket=" );
		builder.append( destinationSocket );
		builder.append( ", data=" );
		builder.append( Arrays.toString( data ) );
		builder.append( "]" );
		return builder.toString();
	}

}
