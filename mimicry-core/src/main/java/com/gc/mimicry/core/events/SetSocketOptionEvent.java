package com.gc.mimicry.core.events;

import java.util.UUID;

import com.gc.mimicry.core.event.BaseEvent;

public class SetSocketOptionEvent extends BaseEvent
{
    private static final long serialVersionUID = -7803180571324691202L;
    private int intValue;
    private boolean boolValue;
    private SocketOption option;

    public SetSocketOptionEvent(UUID controlFlowId, SocketOption option, boolean boolValue)
    {
        this.boolValue = boolValue;
        this.option = option;
    }

    public SetSocketOptionEvent(UUID controlFlowId, SocketOption option, int intValue)
    {
        this.intValue = intValue;
        this.option = option;
    }

    public int getIntValue()
    {
        return intValue;
    }

    public boolean isBoolValue()
    {
        return boolValue;
    }

    public SocketOption getOption()
    {
        return option;
    }

    public static enum SocketOption
    {
        KEEP_ALIVE, OOB_INLINE, RECEIVE_BUFFER_SIZE, REUSE_ADDRESS, SEND_BUFFER_SIZE, SO_LINGER, SO_TIMEOUT, TCP_NO_DELAY, TRAFFIC_CLASS
    }
}
