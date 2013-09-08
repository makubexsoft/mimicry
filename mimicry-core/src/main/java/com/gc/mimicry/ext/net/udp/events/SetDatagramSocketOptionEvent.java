package com.gc.mimicry.ext.net.udp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SetDatagramSocketOptionEvent extends Event
{
    public InetSocketAddress getSocketAddres();

    public void setSocketAddres(InetSocketAddress value);

    public DatagramSocketOption getOption();

    public void setOption(DatagramSocketOption value);

    public int getIntValue();

    public void setIntValue(int value);

    public boolean isBoolValue();

    public void setBoolValue(boolean value);
}
