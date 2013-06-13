package com.gc.mimicry.bridge.net;

import java.util.UUID;

import com.gc.mimicry.core.VectorClock;
import com.gc.mimicry.core.event.Event;

public class SocketConnectionRequest implements Event
{

	public boolean isResponseTo( Event request )
	{
		// TODO Auto-generated method stub
		return false;
	}

	public VectorClock<UUID> getVectorClock()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
