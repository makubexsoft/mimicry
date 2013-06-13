package com.gc.mimicry.net;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.SucceededChannelFuture;

public class TimingChannelEvent implements ChannelEvent
{

	private final Channel	channel;
	private final int		periodInMillis;
	private String			timerName;

	public TimingChannelEvent(Channel channel, int periodInMillis, String timerName)
	{
		this.channel = channel;
		this.periodInMillis = periodInMillis;
		this.timerName = timerName;
	}

	public int getPeriodInMillis()
	{
		return periodInMillis;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public ChannelFuture getFuture()
	{
		return new SucceededChannelFuture( channel );
	}

	public String getTimerName()
	{
		return timerName;
	}
}
