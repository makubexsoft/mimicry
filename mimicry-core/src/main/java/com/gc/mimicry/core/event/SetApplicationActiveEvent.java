package com.gc.mimicry.core.event;

import java.util.UUID;


public class SetApplicationActiveEvent implements Event {
	
	private boolean active;
	
	public boolean isActive()
	{
		return active;
	}

	@Override
	public UUID getControlFlowId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getDestinationAppId() {
		// TODO Auto-generated method stub
		return null;
	}
}
