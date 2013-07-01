package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.Event;

public class SocketBoundEvent implements Event
{
    private final int port;

    public SocketBoundEvent(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public UUID getControlFlowId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID getDestinationAppId()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
 