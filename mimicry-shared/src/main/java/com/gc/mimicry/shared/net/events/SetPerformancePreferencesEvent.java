package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetPerformancePreferencesEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 3426612354615334209L;
	private final InetSocketAddress	socketAddress;
	private final int				connectionTime;
	private final int				latency;
	private final int				bandwidth;

	public SetPerformancePreferencesEvent(InetSocketAddress socketAddress, int connectionTime, int latency, int bandwidth)
	{
		this.socketAddress = socketAddress;
		this.connectionTime = connectionTime;
		this.latency = latency;
		this.bandwidth = bandwidth;
	}

	public InetSocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	public int getConnectionTime()
	{
		return connectionTime;
	}

	public int getLatency()
	{
		return latency;
	}

	public int getBandwidth()
	{
		return bandwidth;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SetPerformancePreferencesEvent [connectionTime=" );
		builder.append( connectionTime );
		builder.append( ", latency=" );
		builder.append( latency );
		builder.append( ", bandwidth=" );
		builder.append( bandwidth );
		builder.append( "]" );
		return builder.toString();
	}

}
