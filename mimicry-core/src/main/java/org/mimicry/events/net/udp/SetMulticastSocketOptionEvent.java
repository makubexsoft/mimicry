package org.mimicry.events.net.udp;

import java.net.InetSocketAddress;

import org.mimicry.engine.ApplicationEvent;


public interface SetMulticastSocketOptionEvent extends ApplicationEvent
{
    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public MulticastSocketOption getOption();

    public void setOption(MulticastSocketOption value);

    public Object getValue();

    public void setValue(Object value);
}
