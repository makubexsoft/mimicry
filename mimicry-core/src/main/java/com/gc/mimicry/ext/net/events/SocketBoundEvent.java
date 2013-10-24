package com.gc.mimicry.ext.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface SocketBoundEvent extends ApplicationEvent
{
    public InetSocketAddress getAddress();

    public void setAddress(InetSocketAddress value);
}
