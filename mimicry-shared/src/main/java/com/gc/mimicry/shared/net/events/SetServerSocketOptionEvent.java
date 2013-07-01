package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.Event;

public class SetServerSocketOptionEvent implements Event
{
    private ServerSocketOption option;
    private int intValue;
    private boolean boolValue;

    public SetServerSocketOptionEvent(ServerSocketOption option, int value)
    {

    }

    public SetServerSocketOptionEvent(ServerSocketOption option, boolean value)
    {

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
