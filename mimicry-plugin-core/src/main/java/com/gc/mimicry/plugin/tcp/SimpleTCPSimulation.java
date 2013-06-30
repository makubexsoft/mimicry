package com.gc.mimicry.plugin.tcp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.event.Event;
import com.gc.mimicry.core.event.EventHandlerBase;
import com.gc.mimicry.core.event.EventHandlerContext;
import com.gc.mimicry.core.runtime.Configurable;

public class SimpleTCPSimulation extends EventHandlerBase implements Configurable
{
	private static final Logger logger;
	static {
		logger = LoggerFactory.getLogger(SimpleTCPSimulation.class);
	}
	
	
	@Override
	public void configure(Map<String, String> configuration) {
		logger.info(configuration.toString());
	}
	
	@Override
	public void handleDownstream(EventHandlerContext ctx, Event evt) {
		logger.info("downstream: " + evt);
	}

	
	@Override
	public void handleUpstream(EventHandlerContext ctx, Event evt) {
		logger.info("upstream: " + evt);
	}
	
}
