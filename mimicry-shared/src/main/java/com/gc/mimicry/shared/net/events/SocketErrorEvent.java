package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.Event;

public class SocketErrorEvent implements Event
{
    private final String message;

    public SocketErrorEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
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
