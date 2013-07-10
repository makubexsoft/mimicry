package com.gc.mimicry.shared.events.clock;

import com.gc.mimicry.shared.events.BaseEvent;

public class ClockAdvanceEvent extends BaseEvent implements ClockEvent
{
	private static final long	serialVersionUID	= -5009070180310534145L;
	private final long			deltaMillis;

	public ClockAdvanceEvent(long deltaMillis)
	{
		this.deltaMillis = deltaMillis;
	}

	public long getDeltaMillis()
	{
		return deltaMillis;
	}
}
