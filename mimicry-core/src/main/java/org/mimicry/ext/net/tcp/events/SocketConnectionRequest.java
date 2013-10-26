package org.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import org.mimicry.engine.event.ApplicationEvent;


public interface SocketConnectionRequest extends ApplicationEvent
{
    public InetSocketAddress getSource();

    public void setSource(InetSocketAddress value);

    public InetSocketAddress getDestination();

    public void setDestination(InetSocketAddress value);
}
