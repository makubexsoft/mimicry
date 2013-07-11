package com.gc.mimicry.shared.events.console;

import com.gc.mimicry.shared.events.BaseEvent;

/**
 * This event represents input data to a application's stdin stream. Use this
 * event to send input data to an application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ConsoleInputEvent extends BaseEvent
{
	private static final long	serialVersionUID	= -1287572195695207848L;
	private final byte[]		data;

	public ConsoleInputEvent(byte[] data)
	{
		super();
		this.data = data;
	}

	public byte[] getData()
	{
		return data;
	}

}
