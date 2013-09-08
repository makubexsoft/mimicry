package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SocketAwaitingConnectionEvent extends Event
{
    public InetSocketAddress getLocalAddress();

    public void setLocalAddress(InetSocketAddress value);
}
