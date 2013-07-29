package com.gc.mimicry.ext.timing.events;

import com.gc.mimicry.engine.BaseEvent;

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
