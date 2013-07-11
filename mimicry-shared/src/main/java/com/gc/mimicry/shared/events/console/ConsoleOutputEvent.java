package com.gc.mimicry.shared.events.console;

import com.gc.mimicry.shared.events.BaseEvent;

/**
 * This event represents data written to stdout of a certain application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ConsoleOutputEvent extends BaseEvent
{
	private static final long	serialVersionUID	= -6312747858804388533L;
	private final String		data;

	public ConsoleOutputEvent(String data)
	{
		this.data = data;
	}

	public String getData()
	{
		return data;
	}
}
