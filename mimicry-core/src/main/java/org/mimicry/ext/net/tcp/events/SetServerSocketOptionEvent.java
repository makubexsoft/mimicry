package org.mimicry.ext.net.tcp.events;

import java.net.InetSocketAddress;

import org.mimicry.engine.event.ApplicationEvent;


public interface SetServerSocketOptionEvent extends ApplicationEvent
{
    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public ServerSocketOption getOption();

    public void setOption(ServerSocketOption value);

    public int getIntValue();

    public void setIntValue(int value);

    public boolean isBoolValue();

    public void setBoolValue(boolean value);
}
