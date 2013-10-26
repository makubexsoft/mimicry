package org.mimicry.events.net;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SocketBindRequestEvent extends ApplicationEvent
{
    public InetSocketAddress getEndPoint();

    public void setEndPoint(InetSocketAddress value);

    public boolean isReusePort();

    public void setReusePort(boolean value);

    public SocketType getSocketType();

    public void setSocketType(SocketType value);
}
