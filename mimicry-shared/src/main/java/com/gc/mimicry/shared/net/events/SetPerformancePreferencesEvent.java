package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetPerformancePreferencesEvent extends BaseEvent
{
	private static final long	serialVersionUID	= 3426612354615334209L;
	private int connectionTime; 
	private int latency; 
	private int bandwidth;

	public SetPerformancePreferencesEvent(UUID appId, int connectionTime, int latency, int bandwidth)
	{
		super( appId );
		
		this.connectionTime=connectionTime;
		this.latency=latency;
		this.bandwidth=bandwidth;
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
