package com.gc.mimicry.ext.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface SocketBindRequestEvent extends ApplicationEvent
{
    public InetSocketAddress getEndPoint();

    public void setEndPoint(InetSocketAddress value);

    public boolean isReusePort();

    public void setReusePort(boolean value);

    public SocketType getSocketType();

    public void setSocketType(SocketType value);
}
