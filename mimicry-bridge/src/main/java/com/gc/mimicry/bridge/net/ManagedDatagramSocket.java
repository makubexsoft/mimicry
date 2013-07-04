package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.cflow.CFlowManager;
import com.gc.mimicry.bridge.cflow.ControlFlow;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.shared.events.BaseEvent;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.DatagramSocketOption;
import com.gc.mimicry.shared.net.events.SetDatagramSocketOptionEvent;
import com.gc.mimicry.shared.net.events.SocketBindRequestEvent;
import com.gc.mimicry.shared.net.events.SocketBoundEvent;
import com.gc.mimicry.shared.net.events.SocketClosedEvent;
import com.gc.mimicry.shared.net.events.SocketErrorEvent;
import com.gc.mimicry.shared.net.events.SocketType;
import com.gc.mimicry.shared.net.events.UDPPacketEvent;

/**
 * Stub implementation of a {@link DatagramSocket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedDatagramSocket extends DatagramSocket
{

    public ManagedDatagramSocket() throws SocketException
    {
        bind(new InetSocketAddress(0));
    }

    // protected ManagedDatagramSocket(DatagramSocketImpl impl)
    // {
    // throw new UnsupportedOperationException("Custom socket implementation are not supported.");
    // }

    public ManagedDatagramSocket(SocketAddress bindaddr) throws SocketException
    {
        if (bindaddr != null)
        {
            bind(bindaddr);
        }
    }

    public ManagedDatagramSocket(int port) throws SocketException
    {
        this(port, null);
    }

    public ManagedDatagramSocket(int port, InetAddress laddr) throws SocketException
    {
        this(new InetSocketAddress(laddr, port));
    }

    void checkAddress(InetAddress addr, String op)
    {
        if (addr == null)
        {
            return;
        }
        if (!(addr instanceof Inet4Address || addr instanceof Inet6Address))
        {
            throw new IllegalArgumentException(op + ": invalid address type");
        }
    }

    private synchronized void connectInternal(InetAddress address, int port) throws SocketException
    {
        if (port < 0 || port > 0xFFFF)
        {
            throw new IllegalArgumentException("connect: " + port);
        }
        if (address == null)
        {
            throw new IllegalArgumentException("connect: null address");
        }
        checkAddress(address, "connect");
        if (isClosed())
        {
            return;
        }

        if (!isBound())
        {
            bind(new InetSocketAddress(0));
        }

        connected = true;
        connectedAddress = new InetSocketAddress(address, port);
    }

    @Override
    public synchronized void bind(SocketAddress addr) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (isBound())
        {
            throw new SocketException("already bound");
        }
        if (addr == null)
        {
            addr = new InetSocketAddress(0);
        }
        if (!(addr instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type!");
        }
        InetSocketAddress epoint = (InetSocketAddress) addr;
        if (epoint.isUnresolved())
        {
            throw new SocketException("Unresolved address");
        }
        InetAddress iaddr = epoint.getAddress();
        checkAddress(iaddr, "bind");

        SocketBindRequestEvent evt = new SocketBindRequestEvent(epoint, SocketType.UDP, reuseAddress);

        Event event = processEvent(evt);

        if (event instanceof SocketBoundEvent)
        {
            localAddress = ((SocketBoundEvent) event).getAddress();
            bound = true;
        }
        else
        {
            SocketErrorEvent error = (SocketErrorEvent) event;
            throw new SocketException("Failed to bind socket: " + error.getMessage());
        }
    }

    @Override
    public void connect(InetAddress address, int port)
    {
        try
        {
            connectInternal(address, port);
        }
        catch (SocketException se)
        {
            throw new Error("connect failed", se);
        }
    }

    @Override
    public void connect(SocketAddress addr) throws SocketException
    {
        if (addr == null)
        {
            throw new IllegalArgumentException("Address can't be null");
        }
        if (!(addr instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }
        InetSocketAddress epoint = (InetSocketAddress) addr;
        if (epoint.isUnresolved())
        {
            throw new SocketException("Unresolved address");
        }
        connectInternal(epoint.getAddress(), epoint.getPort());
    }

    @Override
    public void disconnect()
    {
        synchronized (this)
        {
            if (isClosed())
            {
                return;
            }
            // TODO: disconnect
            connectedAddress = null;
            connected = false;
        }
    }

    @Override
    public boolean isBound()
    {
        return bound;
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public InetAddress getInetAddress()
    {
        return connectedAddress.getAddress();
    }

    @Override
    public int getPort()
    {
        return connectedAddress.getPort();
    }

    @Override
    public SocketAddress getRemoteSocketAddress()
    {
        if (!isConnected())
        {
            return null;
        }
        return new InetSocketAddress(getInetAddress(), getPort());
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        if (isClosed())
        {
            return null;
        }
        if (!isBound())
        {
            return null;
        }
        return new InetSocketAddress(getLocalAddress(), getLocalPort());
    }

    @Override
    public void send(DatagramPacket p) throws IOException
    {
        InetAddress packetAddress = null;
        synchronized (p)
        {
            if (isClosed())
            {
                throw new SocketException("Socket is closed");
            }
            checkAddress(p.getAddress(), "send");
            if (isConnected())
            {
                packetAddress = p.getAddress();
                if (packetAddress == null)
                {
                    p.setAddress(connectedAddress.getAddress());
                    p.setPort(connectedAddress.getPort());
                }
                else if ((!packetAddress.equals(connectedAddress.getAddress()))
                        || p.getPort() != connectedAddress.getPort())
                {
                    throw new IllegalArgumentException("connected address " + "and packet address" + " differ");
                }
            }
            if (!isBound())
            {
                bind(new InetSocketAddress(0));
            }
            emitEvent(new UDPPacketEvent(localAddress, (InetSocketAddress) p.getSocketAddress(), p.getData(), 255));
        }
    }

    @Override
    public synchronized void receive(DatagramPacket p) throws IOException
    {
        synchronized (p)
        {
            if (!isBound())
            {
                bind(new InetSocketAddress(0));
            }
            synchronized (receiveBufferLock)
            {
                while (receiveBuffer.size() == 0)
                {
                    try
                    {
                        SimulatorBridge.getClock().waitOn(receiveBufferLock);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO: review
                    }
                }
                DatagramPacket packet = receiveBuffer.remove(0);
                p.setSocketAddress(packet.getSocketAddress());
                p.setData(packet.getData());
            }
        }
    }

    @Override
    public InetAddress getLocalAddress()
    {
        if (isClosed())
        {
            return null;
        }
        return localAddress.getAddress();
    }

    @Override
    public int getLocalPort()
    {
        if (isClosed())
        {
            return -1;
        }
        return localAddress.getPort();
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.SO_TIMEOUT, timeout));
        socketTimeout = timeout;
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return socketTimeout;
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException
    {
        if (!(size > 0))
        {
            throw new IllegalArgumentException("negative send size");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.SEND_BUFFER_SIZE, size));
        sendBufferSize = size;
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return sendBufferSize;
    }

    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("invalid receive size");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.RECEIVE_BUFFER_SIZE, size));
        receiveBufferSize = size;
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return receiveBufferSize;
    }

    @Override
    public synchronized void setReuseAddress(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.REUSE_ADDRESS, on));
        reuseAddress = on;
    }

    @Override
    public synchronized boolean getReuseAddress() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return reuseAddress;
    }

    @Override
    public synchronized void setBroadcast(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.BROADCAST, on));
        broadcast = on;
    }

    @Override
    public synchronized boolean getBroadcast() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return broadcast;
    }

    @Override
    public synchronized void setTrafficClass(int tc) throws SocketException
    {
        if (tc < 0 || tc > 255)
        {
            throw new IllegalArgumentException("tc is not in range 0 -- 255");
        }

        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetDatagramSocketOptionEvent(localAddress, DatagramSocketOption.TRAFFIC_CLASS, tc));
        trafficClass = tc;
    }

    @Override
    public synchronized int getTrafficClass() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return trafficClass;
    }

    @Override
    public void close()
    {
        if (isClosed())
        {
            return;
        }
        SocketClosedEvent evt = new SocketClosedEvent();
        emitEvent(evt);
        cflowMgr.terminateAll(evt);
        closed = true;
    }

    @Override
    public boolean isClosed()
    {
        return closed;
    }

    @Override
    public DatagramChannel getChannel()
    {
        return null;
    }

    protected Event processEvent(BaseEvent evt)
    {
        ControlFlow controlFlow = cflowMgr.createControlFlow(evt);
        Event event = controlFlow.awaitTermination();
        return event;
    }

    protected void emitEvent(BaseEvent evt)
    {
        evt.setSourceApp(SimulatorBridge.getApplicationId());
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
    }

    protected InetSocketAddress getConnectedAddress()
    {
        return connectedAddress;
    }

    private boolean bound = false;
    private boolean closed = false;
    private InetSocketAddress connectedAddress;
    private InetSocketAddress localAddress;
    private boolean connected;
    private int socketTimeout;
    private int sendBufferSize;
    private int receiveBufferSize;
    private boolean reuseAddress;
    private boolean broadcast;
    private int trafficClass;
    private final CFlowManager cflowMgr;
    private final Object receiveBufferLock = new Object();
    private final List<DatagramPacket> receiveBuffer = new ArrayList<DatagramPacket>();
    // default construction
    {
        cflowMgr = new CFlowManager(SimulatorBridge.getApplicationId(), SimulatorBridge.getEventBridge());
        cflowMgr.addHandler(UDPPacketEvent.class, new EventListener()
        {

            @Override
            public void handleEvent(Event evt)
            {
                UDPPacketEvent data = (UDPPacketEvent) evt;

                // filtering by IP must be done in event handler
                if (data.getDestination().getPort() == localAddress.getPort())
                {
                    synchronized (receiveBufferLock)
                    {
                        try
                        {
                            receiveBuffer.add(new DatagramPacket(data.getData(), data.getData().length, data
                                    .getSource()));
                            SimulatorBridge.getClock().notifyAllOnTarget(receiveBufferLock);
                        }
                        catch (SocketException e)
                        {
                            // won't be the case
                        }
                    }
                }
            }
        });
    }
}
