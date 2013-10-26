package org.mimicry.bridge.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;

import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.threading.ManagedThread;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.event.EventFactory;
import org.mimicry.ext.net.udp.events.JoinMulticastGroupEvent;
import org.mimicry.ext.net.udp.events.LeaveMulticastGroupEvent;
import org.mimicry.ext.net.udp.events.MulticastSocketOption;
import org.mimicry.ext.net.udp.events.SetMulticastSocketOptionEvent;
import org.mimicry.ext.net.udp.events.UDPPacketEvent;


/**
 * Stub implementation of a {@link MulticastSocket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedMulticastSocket extends ManagedDatagramSocket
{
    /**
     * Used on some platforms to record if an outgoing interface has been set for this socket.
     */
    private boolean interfaceSet;

    public ManagedMulticastSocket() throws IOException
    {
        this(new InetSocketAddress(0));
    }

    public ManagedMulticastSocket(int port) throws IOException
    {
        this(new InetSocketAddress(port));
    }

    public ManagedMulticastSocket(SocketAddress bindaddr) throws IOException
    {
        super(null);

        // Enable SO_REUSEADDR before binding
        setReuseAddress(true);

        if (bindaddr != null)
        {
            bind(bindaddr);
        }
    }

    /**
     * The lock on the socket's interface - used by setInterface and getInterface
     */
    private final Object infLock = new Object();

    /**
     * The "last" interface set by setInterface on this MulticastSocket
     */
    private InetAddress infAddress = null;

    private int ttl;

    private <T extends ApplicationEvent> T createEvent(Class<T> eventClass)
    {
        EventFactory eventFactory = ManagedThread.currentThread().getEventFactory();
        return eventFactory.createEvent(eventClass, SimulatorBridge.getApplicationId());
    }

    @Deprecated
    public void setTTL(byte ttl) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetMulticastSocketOptionEvent event = createEvent(SetMulticastSocketOptionEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setOption(MulticastSocketOption.TTL);
        event.setValue(ttl);
        emitEvent(event);

        this.ttl = ttl & 0xFF;
    }

    public void setTimeToLive(int ttl) throws IOException
    {
        if (ttl < 0 || ttl > 255)
        {
            throw new IllegalArgumentException("ttl out of range");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetMulticastSocketOptionEvent event = createEvent(SetMulticastSocketOptionEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setOption(MulticastSocketOption.TTL);
        event.setValue(ttl);
        emitEvent(event);

        this.ttl = ttl;
    }

    @Deprecated
    public byte getTTL() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return (byte) ttl;
    }

    public int getTimeToLive() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return ttl;
    }

    public void joinGroup(InetAddress mcastaddr) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        checkAddress(mcastaddr, "joinGroup");

        if (!mcastaddr.isMulticastAddress())
        {
            throw new SocketException("Not a multicast address");
        }

        // /**
        // * required for some platforms where it's not possible to join a group without setting the interface first.
        // */
        // NetworkInterface defaultInterface = NetworkInterface.getDefault();
        //
        // if (!interfaceSet && defaultInterface != null)
        // {
        // setNetworkInterface(defaultInterface);
        // }

        JoinMulticastGroupEvent event = createEvent(JoinMulticastGroupEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setGroupAddress(mcastaddr);
        emitEvent(event);
    }

    public void leaveGroup(InetAddress mcastaddr) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        checkAddress(mcastaddr, "leaveGroup");

        if (!mcastaddr.isMulticastAddress())
        {
            throw new SocketException("Not a multicast address");
        }

        LeaveMulticastGroupEvent event = createEvent(LeaveMulticastGroupEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setGroupAddress(mcastaddr);
        emitEvent(event);
    }

    public void joinGroup(SocketAddress mcastaddr, NetworkInterface netIf) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        if (mcastaddr == null || !(mcastaddr instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }

        checkAddress(((InetSocketAddress) mcastaddr).getAddress(), "joinGroup");

        if (!((InetSocketAddress) mcastaddr).getAddress().isMulticastAddress())
        {
            throw new SocketException("Not a multicast address");
        }

        JoinMulticastGroupEvent event = createEvent(JoinMulticastGroupEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setNetworkInterface(netIf);
        event.setGroupAddress(((InetSocketAddress) mcastaddr).getAddress());
        emitEvent(event);
    }

    public void leaveGroup(SocketAddress mcastaddr, NetworkInterface netIf) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        if (mcastaddr == null || !(mcastaddr instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }

        checkAddress(((InetSocketAddress) mcastaddr).getAddress(), "leaveGroup");

        if (!((InetSocketAddress) mcastaddr).getAddress().isMulticastAddress())
        {
            throw new SocketException("Not a multicast address");
        }

        LeaveMulticastGroupEvent event = createEvent(LeaveMulticastGroupEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setNetworkInterface(netIf);
        event.setGroupAddress(((InetSocketAddress) mcastaddr).getAddress());
        emitEvent(event);
    }

    public void setInterface(InetAddress inf) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        checkAddress(inf, "setInterface");
        synchronized (infLock)
        {
            SetMulticastSocketOptionEvent event = createEvent(SetMulticastSocketOptionEvent.class);
            event.setSocketAddress(getLocalInetSocketAddress());
            event.setOption(MulticastSocketOption.IFC_BY_INET_ADDRESS);
            event.setValue(inf);
            emitEvent(event);

            infAddress = inf;
            interfaceSet = true;
        }
    }

    // public InetAddress getInterface() throws SocketException
    // {
    // if (isClosed())
    // {
    // throw new SocketException("Socket is closed");
    // }
    // synchronized (infLock)
    // {
    // InetAddress ia = (InetAddress) getImpl().getOption(SocketOptions.IP_MULTICAST_IF);
    //
    // /**
    // * No previous setInterface or interface can be set using setNetworkInterface
    // */
    // if (infAddress == null)
    // {
    // return ia;
    // }
    //
    // /**
    // * Same interface set with setInterface?
    // */
    // if (ia.equals(infAddress))
    // {
    // return ia;
    // }
    //
    // /**
    // * Different InetAddress from what we set with setInterface so enumerate the current interface to see if the
    // * address set by setInterface is bound to this interface.
    // */
    // try
    // {
    // NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
    // Enumeration addrs = ni.getInetAddresses();
    // while (addrs.hasMoreElements())
    // {
    // InetAddress addr = (InetAddress) (addrs.nextElement());
    // if (addr.equals(infAddress))
    // {
    // return infAddress;
    // }
    // }
    //
    // /**
    // * No match so reset infAddress to indicate that the interface has changed via means
    // */
    // infAddress = null;
    // return ia;
    // }
    // catch (Exception e)
    // {
    // return ia;
    // }
    // }
    // }

    private NetworkInterface netIf;

    public void setNetworkInterface(NetworkInterface netIf) throws SocketException
    {
        synchronized (infLock)
        {
            SetMulticastSocketOptionEvent event = createEvent(SetMulticastSocketOptionEvent.class);
            event.setSocketAddress(getLocalInetSocketAddress());
            event.setOption(MulticastSocketOption.IFC_BY_NETWORK_INTERFACE);
            event.setValue(netIf);
            emitEvent(event);

            infAddress = null;
            interfaceSet = true;
            this.netIf = netIf;
        }
    }

    // public NetworkInterface getNetworkInterface() throws SocketException
    // {
    // NetworkInterface ni = (NetworkInterface) getImpl().getOption(SocketOptions.IP_MULTICAST_IF2);
    // if (ni.getIndex() == 0)
    // {
    // InetAddress[] addrs = new InetAddress[1];
    // addrs[0] = InetAddress.anyLocalAddress();
    // return new NetworkInterface(addrs[0].getHostName(), 0, addrs);
    // }
    // else
    // {
    // return ni;
    // }
    // }

    private boolean loopbackMode;

    public void setLoopbackMode(boolean disable) throws SocketException
    {
        SetMulticastSocketOptionEvent event = createEvent(SetMulticastSocketOptionEvent.class);
        event.setSocketAddress(getLocalInetSocketAddress());
        event.setOption(MulticastSocketOption.IP_MULTICAST_LOOP);
        event.setValue(disable);
        emitEvent(event);

        loopbackMode = disable;
    }

    private InetSocketAddress getLocalInetSocketAddress()
    {
        return (InetSocketAddress) getLocalSocketAddress();
    }

    public boolean getLoopbackMode() throws SocketException
    {
        return loopbackMode;
    }

    @Deprecated
    public void send(DatagramPacket p, byte ttl) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        checkAddress(p.getAddress(), "send");
        synchronized (p)
        {
            if (isConnected())
            {
                InetAddress packetAddress = p.getAddress();
                if (packetAddress == null)
                {
                    p.setAddress(getConnectedAddress().getAddress());
                    p.setPort(getConnectedAddress().getPort());
                }
                else if ((!packetAddress.equals(getConnectedAddress().getAddress()))
                        || p.getPort() != getConnectedAddress().getPort())
                {
                    throw new SecurityException("connected address and packet address differ");
                }
            }

            UDPPacketEvent event = createEvent(UDPPacketEvent.class);
            event.setSource(getLocalInetSocketAddress());
            event.setDestination((InetSocketAddress) p.getSocketAddress());
            event.setData(p.getData());
            event.setTimeToLive(ttl);
            emitEvent(event);
        }
    }
}
