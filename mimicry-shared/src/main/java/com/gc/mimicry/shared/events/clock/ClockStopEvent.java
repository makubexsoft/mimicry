package com.gc.mimicry.shared.events.clock;

import com.gc.mimicry.shared.events.BaseEvent;

/**
 * Stops the real-time clock iff installed. This event is evaluated by all
 * ClockDriver instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ClockStopEvent extends BaseEvent implements ClockEvent
{

	private static final long	serialVersionUID	= -2218736486062497535L;

}
