package com.gc.mimicry.shared.net.events;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetPerformancePreferencesEvent extends BaseEvent
{
	private static final long	serialVersionUID	= 3426612354615334209L;
	private final int			connectionTime;
	private final int			latency;
	private final int			bandwidth;

	public SetPerformancePreferencesEvent(int connectionTime, int latency, int bandwidth)
	{
		this.connectionTime = connectionTime;
		this.latency = latency;
		this.bandwidth = bandwidth;
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
}
