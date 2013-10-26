package org.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import org.mimicry.engine.event.ApplicationEvent;


public interface SetPerformancePreferencesEvent extends ApplicationEvent
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
