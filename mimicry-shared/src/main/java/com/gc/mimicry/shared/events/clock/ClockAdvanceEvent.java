package com.gc.mimicry.shared.events.clock;

import com.gc.mimicry.shared.events.BaseEvent;

/**
 * This event type advances the discrete clock by a certain amount of time iff
 * installed. This event is evaluated by all ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockAdvanceEvent extends BaseEvent implements ClockEvent
{
	private static final long	serialVersionUID	= -5009070180310534145L;
	private final long			deltaMillis;

	public ClockAdvanceEvent(long deltaMillis)
	{
		this.deltaMillis = deltaMillis;
	}

	/**
	 * The delta T (in milliseconds) by which the clock should be advanced.
	 * 
	 * @return
	 */
	public long getDeltaMillis()
	{
		return deltaMillis;
	}
}
