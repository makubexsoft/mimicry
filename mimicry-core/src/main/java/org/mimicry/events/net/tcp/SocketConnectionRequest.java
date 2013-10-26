package org.mimicry.events.net.tcp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SocketConnectionRequest extends ApplicationEvent
{
    public InetSocketAddress getSource();

    public void setSource(InetSocketAddress value);

    public InetSocketAddress getDestination();

    public void setDestination(InetSocketAddress value);
}
