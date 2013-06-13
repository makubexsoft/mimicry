package com.gc.mimicry.core.timing.net;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.timing.ClockType;

public class InstallClockMessage extends Message
{
	private static final long	serialVersionUID	= 8971737035766242022L;
	private final UUID			id;
	private final ClockType		clockType;

	public InstallClockMessage(ClockType clockType)
	{
		this.clockType = clockType;
		id = UUID.randomUUID();
	}

	public ClockType getClockType()
	{
		return clockType;
	}

	public UUID getId()
	{
		return id;
	}
}
