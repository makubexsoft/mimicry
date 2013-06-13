package com.gc.mimicry.core.timing.net;

import com.gc.mimicry.core.messaging.Message;

public class StartClockMessage extends Message
{
	private static final long	serialVersionUID	= 1330844020092657297L;
	private final double		multiplier;

	public StartClockMessage(double multiplier)
	{
		this.multiplier = multiplier;
	}

	public double getMultiplier()
	{
		return multiplier;
	}
}
