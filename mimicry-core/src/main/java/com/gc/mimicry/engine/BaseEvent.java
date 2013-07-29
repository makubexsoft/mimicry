package com.gc.mimicry.engine;

import java.util.UUID;

import com.gc.mimicry.util.VectorClock;

/**
 * Base class for events for convenience.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class BaseEvent implements Event
{
	public void setControlFlowId( UUID controlFlowId )
	{
		this.controlFlowId = controlFlowId;
	}

	public void setTargetApp( UUID targetApp )
	{
		this.targetApp = targetApp;
	}

	public void setSourceApp( UUID sourceApp )
	{
		this.sourceApp = sourceApp;
	}

	@Override
	public UUID getAssociatedControlFlow()
	{
		return controlFlowId;
	}

	@Override
	public UUID getSourceApplication()
	{
		return sourceApp;
	}

	@Override
	public UUID getTargetApplication()
	{
		return targetApp;
	}

	public void applyToClock( VectorClock<UUID> remoteClock )
	{
		// remoteClock.tick()
		// this.lock = remoteClock.clone()
		// TODO: should we perform the tick within this class or let the client
		// do?
	}

	private static final long	serialVersionUID	= 1656650145236686849L;
	private UUID				controlFlowId;
	private UUID				sourceApp;
	private UUID				targetApp;
	private VectorClock<UUID>	clock;
}
