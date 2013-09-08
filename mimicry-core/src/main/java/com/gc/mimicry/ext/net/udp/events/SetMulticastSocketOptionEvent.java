package com.gc.mimicry.ext.net.udp.events;

import java.net.InetSocketAddress;

import com.gc.mimicry.engine.event.Event;

public interface SetMulticastSocketOptionEvent extends Event
{
    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public MulticastSocketOption getOption();

    public void setOption(MulticastSocketOption value);

    public Object getValue();

    public void setValue(Object value);
}
