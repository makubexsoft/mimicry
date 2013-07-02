package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.gc.mimicry.shared.events.BaseEvent;

public class TCPReceivedDataEvent extends BaseEvent
{
	private static final long		serialVersionUID	= -7671370808381569639L;
	private final InetSocketAddress	sourceSocket;
	private final InetSocketAddress	destinationSocket;
	private final byte[]			data;

	public TCPReceivedDataEvent(InetSocketAddress sourceSocket, InetSocketAddress destinationSocket, byte[] data)
	{
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
		builder.append( "TCPReceivedDataEvent [sourceSocket=" );
		builder.append( sourceSocket );
		builder.append( ", destinationSocket=" );
		builder.append( destinationSocket );
		builder.append( ", data=" );
		builder.append( Arrays.toString( data ) );
		builder.append( "]" );
		return builder.toString();
	}

}
