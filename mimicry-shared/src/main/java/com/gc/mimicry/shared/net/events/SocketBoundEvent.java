package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketBoundEvent extends BaseEvent
{
	private static final long	serialVersionUID	= -696695113477236492L;
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

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "SocketBoundEvent [address=" );
		builder.append( address );
		builder.append( "]" );
		return builder.toString();
	}
    
    
}
 