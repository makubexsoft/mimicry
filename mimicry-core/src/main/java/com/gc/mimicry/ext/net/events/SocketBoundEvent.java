package com.gc.mimicry.ext.net.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SocketBoundEvent extends Event
{
    public InetSocketAddress getAddress();

    public void setAddress(InetSocketAddress value);
}
