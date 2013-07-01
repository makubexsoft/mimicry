package com.gc.mimicry.shared.net.events;

import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketBindRequestEvent extends BaseEvent
{
    private static final long serialVersionUID = 1L;
    private final int port;
    private final boolean reusePort;

    public SocketBindRequestEvent(UUID appId, UUID cflowId, int port, boolean reusePort)
    {
        super(appId, cflowId);

        this.port = port;
        this.reusePort = reusePort;
    }

    public int getPort()
    {
        return port;
    }

    public boolean isReusePort()
    {
        return reusePort;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SocketBindRequestEvent [port=");
        builder.append(port);
        builder.append(", reusePort=");
        builder.append(reusePort);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
