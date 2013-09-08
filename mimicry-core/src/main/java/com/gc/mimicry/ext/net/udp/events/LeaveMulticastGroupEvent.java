package com.gc.mimicry.ext.net.udp.events;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import com.gc.mimicry.engine.event.Event;

public interface LeaveMulticastGroupEvent extends Event
{
    public NetworkInterface getNetworkInterface();

    public void setNetworkInterface(NetworkInterface value);

    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public InetAddress getGroupAddress();

    public void setGroupAddress(InetAddress value);
}
