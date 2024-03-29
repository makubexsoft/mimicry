package org.mimicry.events.net.tcp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface TCPReceivedDataEvent extends ApplicationEvent
{

    public InetSocketAddress getSourceSocket();

    public void setSourceSocket(InetSocketAddress value);

    public InetSocketAddress getDestinationSocket();

    public void setDestinationSocket(InetSocketAddress value);

    public byte[] getData();

    public void setData(byte[] value);
}
