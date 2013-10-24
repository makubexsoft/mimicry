package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface SocketAwaitingConnectionEvent extends ApplicationEvent
{
    public InetSocketAddress getLocalAddress();

    public void setLocalAddress(InetSocketAddress value);
}
