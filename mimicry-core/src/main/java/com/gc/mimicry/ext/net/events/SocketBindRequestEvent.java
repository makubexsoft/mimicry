package com.gc.mimicry.ext.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SocketBindRequestEvent extends Event
{
    public InetSocketAddress getEndPoint();

    public void setEndPoint(InetSocketAddress value);

    public boolean isReusePort();

    public void setReusePort(boolean value);

    public SocketType getSocketType();

    public void setSocketType(SocketType value);
}
