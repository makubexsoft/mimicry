package org.mimicry.events.net.udp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SetDatagramSocketOptionEvent extends ApplicationEvent
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
