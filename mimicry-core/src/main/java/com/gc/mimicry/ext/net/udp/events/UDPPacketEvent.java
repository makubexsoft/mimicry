package com.gc.mimicry.ext.net.udp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface UDPPacketEvent extends ApplicationEvent
{
    public int getTimeToLive();

    public void setTimeToLive(int value);

    public InetSocketAddress getSource();

    public void setSource(InetSocketAddress value);

    public InetSocketAddress getDestination();

    public void setDestination(InetSocketAddress value);

    public byte[] getData();

    public void setData(byte[] value);
}
