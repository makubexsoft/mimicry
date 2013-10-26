package org.mimicry.events.net.tcp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SocketAwaitingConnectionEvent extends ApplicationEvent
{
    public InetSocketAddress getLocalAddress();

    public void setLocalAddress(InetSocketAddress value);
}
