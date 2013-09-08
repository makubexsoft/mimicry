package com.gc.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SetPerformancePreferencesEvent extends Event
{
    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public int getConnectionTime();

    public void setConnectionTime(int value);

    public int getLatency();

    public void setLatency(int value);

    public int getBandwidth();

    public void setBandwidth(int value);
}
