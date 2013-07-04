package com.gc.mimicry.shared.net.events;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import com.gc.mimicry.shared.events.BaseEvent;

public class LeaveMulticastGroupEvent extends BaseEvent
{
	private static final long		serialVersionUID	= 2293290339996225436L;
	private final InetSocketAddress	socketAddress;
	private NetworkInterface		networkInterface;
	private final InetAddress		groupAddress;

	public LeaveMulticastGroupEvent(InetSocketAddress socketAddress, NetworkInterface networkInterface,
			InetAddress groupAddress)
	{
		this.socketAddress = socketAddress;
		this.networkInterface = networkInterface;
		this.groupAddress = groupAddress;
	}

	public LeaveMulticastGroupEvent(InetSocketAddress socketAddress, InetAddress groupAddress)
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
		builder.append( "LeaveMulticastGroupEvent [socketAddress=" );
		builder.append( socketAddress );
		builder.append( ", networkInterface=" );
		builder.append( networkInterface );
		builder.append( ", groupAddress=" );
		builder.append( groupAddress );
		builder.append( "]" );
		return builder.toString();
	}

}
