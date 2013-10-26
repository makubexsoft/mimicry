package org.mimicry.events.net;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SocketBoundEvent extends ApplicationEvent
{
    public InetSocketAddress getAddress();

    public void setAddress(InetSocketAddress value);
}
