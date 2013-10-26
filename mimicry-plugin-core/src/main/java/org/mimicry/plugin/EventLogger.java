package org.mimicry.plugin;

import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventHandlerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	public void handleDownstream( ApplicationEvent evt )
	{
		logger.info( "downstream event: " + evt );

		sendDownstream( evt );
	}

	@Override
	public void handleUpstream( ApplicationEvent evt )
	{
		logger.info( "upstream event: " + evt );

		sendUpstream( evt );
	}
}
