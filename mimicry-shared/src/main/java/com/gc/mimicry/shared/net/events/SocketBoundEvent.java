package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketBoundEvent extends BaseEvent
{
    private final InetSocketAddress address;

    public SocketBoundEvent(UUID appId, UUID cflow, InetSocketAddress address)
    {
    	super(appId, cflow);
    	
        this.address = address;
    }

    public InetSocketAddress getAddress()
    {
        return address;
    }
}
 