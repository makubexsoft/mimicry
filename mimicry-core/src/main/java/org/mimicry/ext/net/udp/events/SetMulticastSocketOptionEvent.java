package org.mimicry.ext.net.udp.events;

import java.net.InetSocketAddress;

import org.mimicry.engine.event.ApplicationEvent;


public interface SetMulticastSocketOptionEvent extends ApplicationEvent
{
    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public MulticastSocketOption getOption();

    public void setOption(MulticastSocketOption value);

    public Object getValue();

    public void setValue(Object value);
}