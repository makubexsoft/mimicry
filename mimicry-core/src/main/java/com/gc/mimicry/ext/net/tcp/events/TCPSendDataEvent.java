package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface TCPSendDataEvent extends ApplicationEvent
{
    public InetSocketAddress getSourceSocket();

    public void setSourceSocket(InetSocketAddress value);

    public InetSocketAddress getDestinationSocket();

    public void setDestinationSocket(InetSocketAddress value);

    public byte[] getData();

    public void setData(byte[] value);
}
