package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.gc.mimicry.shared.events.BaseEvent;

public class UDPPacketEvent extends BaseEvent
{
	private static final long		serialVersionUID	= -9221227800133838640L;
	private final InetSocketAddress	source;
	private final InetSocketAddress	destination;
	private final byte[]			data;
	private final int				timeToLive;

	public UDPPacketEvent(InetSocketAddress source, InetSocketAddress destination, byte[] data, int timeToLive)
	{
		this.source = source;
		this.destination = destination;
		this.data = data;
		this.timeToLive = timeToLive;
	}

	public int getTimeToLive()
	{
		return timeToLive;
	}

	public InetSocketAddress getSource()
	{
		return source;
	}

	public InetSocketAddress getDestination()
	{
		return destination;
	}

	public byte[] getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "UDPPacketEvent [source=" );
		builder.append( source );
		builder.append( ", destination=" );
		builder.append( destination );
		builder.append( ", data=" );
		builder.append( Arrays.toString( data ) );
		builder.append( "]" );
		return builder.toString();
	}
}
