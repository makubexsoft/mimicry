package com.gc.mimicry.plugin;

import javax.swing.JDialog;

import com.gc.mimicry.core.timing.Clock;

public class TimelineWindowPlugin extends JDialog
{
	private TimelineWindowPlugin(Clock clock)
	{

	}

	public static TimelineWindowPlugin attach( Clock clock )
	{
		return new TimelineWindowPlugin( clock );
	}
}
