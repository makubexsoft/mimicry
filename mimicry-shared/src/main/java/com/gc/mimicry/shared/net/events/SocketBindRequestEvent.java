package com.gc.mimicry.shared.net.events;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.gc.mimicry.shared.events.BaseEvent;

public class SocketBindRequestEvent extends BaseEvent
{
    private static final long serialVersionUID = 1L;
    private final InetSocketAddress endpoint;
    private final boolean reusePort;

    public SocketBindRequestEvent(UUID appId, UUID cflowId, InetSocketAddress endpoint, boolean reusePort)
    {
        super(appId, cflowId);

        this.endpoint = endpoint;
        this.reusePort = reusePort;
    }

    public InetSocketAddress getEndPoint()
    {
        return endpoint;
    }

    public boolean isReusePort()
    {
        return reusePort;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SocketBindRequestEvent [endpoint=");
        builder.append(endpoint);
        builder.append(", reusePort=");
        builder.append(reusePort);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
