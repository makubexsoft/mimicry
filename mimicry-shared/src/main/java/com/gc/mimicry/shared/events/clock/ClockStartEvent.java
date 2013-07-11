package com.gc.mimicry.shared.events.clock;

import com.gc.mimicry.shared.events.BaseEvent;

/**
 * Starts the realtime clock if installed. This event is evaluated by all
 * ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockStartEvent extends BaseEvent implements ClockEvent
{
	private static final long	serialVersionUID	= 348151174134667351L;
	private final double		multiplier;

	public ClockStartEvent(double multiplier)
	{
		this.multiplier = multiplier;
	}

	public double getMultiplier()
	{
		return multiplier;
	}
}
