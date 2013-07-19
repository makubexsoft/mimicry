package com.gc.mimicry.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.shared.events.Event;

/**
 * A simple event handler that writes all events passing up or down to the
 * logging system.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventLogger extends EventHandlerBase
{
	private static final Logger	logger;
	static
	{
		logger = LoggerFactory.getLogger( EventLogger.class );
	}

	@Override
	public void handleDownstream( Event evt )
	{
		logger.info( "downstream event: " + evt );

		sendDownstream( evt );
	}

	@Override
	public void handleUpstream( Event evt )
	{
		logger.info( "upstream event: " + evt );

		sendUpstream( evt );
	}
}
