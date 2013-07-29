package com.gc.mimicry.ext.net.udp.events;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import com.gc.mimicry.engine.BaseEvent;

public class JoinMulticastGroupEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 6963108882325767287L;
	private final InetSocketAddress	socketAddress;
	private NetworkInterface		networkInterface;
	private final InetAddress		groupAddress;

	public JoinMulticastGroupEvent(InetSocketAddress socketAddress, NetworkInterface networkInterface,
			InetAddress groupAddress)
	{
		this.socketAddress = socketAddress;
		this.networkInterface = networkInterface;
		this.groupAddress = groupAddress;
	}

	public JoinMulticastGroupEvent(InetSocketAddress socketAddress, InetAddress groupAddress)
	{
		this.socketAddress = socketAddress;
		this.groupAddress = groupAddress;
	}

	public NetworkInterface getNetworkInterface()
	{
		return networkInterface;
	}

	public InetSocketAddress getSocketAddress()
	{
		return socketAddress;
	}

	public InetAddress getGroupAddress()
	{
		return groupAddress;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "JoinMulticastGroupEvent [socketAddress=" );
		builder.append( socketAddress );
		builder.append( ", networkInterface=" );
		builder.append( networkInterface );
		builder.append( ", groupAddress=" );
		builder.append( groupAddress );
		builder.append( "]" );
		return builder.toString();
	}

}
