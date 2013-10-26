package org.mimicry.events.net.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

import org.mimicry.engine.ApplicationEvent;


public interface LeaveMulticastGroupEvent extends ApplicationEvent
{
    public NetworkInterface getNetworkInterface();

    public void setNetworkInterface(NetworkInterface value);

    public InetSocketAddress getSocketAddress();

    public void setSocketAddress(InetSocketAddress value);

    public InetAddress getGroupAddress();

    public void setGroupAddress(InetAddress value);
}
