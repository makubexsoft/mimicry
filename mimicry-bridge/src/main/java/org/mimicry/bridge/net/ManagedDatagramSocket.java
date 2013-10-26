package org.mimicry.bridge.net;

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

import org.mimicry.bridge.ControlFlow;
import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.cflow.CFlowManager;
import org.mimicry.bridge.threading.ManagedThread;
import org.mimicry.engine.EventListener;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.event.EventFactory;
import org.mimicry.ext.net.events.SocketBindRequestEvent;
import org.mimicry.ext.net.events.SocketBoundEvent;
import org.mimicry.ext.net.events.SocketClosedEvent;
import org.mimicry.ext.net.events.SocketErrorEvent;
import org.mimicry.ext.net.events.SocketType;
import org.mimicry.ext.net.udp.events.DatagramSocketOption;
import org.mimicry.ext.net.udp.events.SetDatagramSocketOptionEvent;
import org.mimicry.ext.net.udp.events.UDPPacketEvent;


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
        parentInstantiated = true;
        assureInit();
        bind(new InetSocketAddress(0));
    }

    // protected ManagedDatagramSocket(DatagramSocketImpl impl)
    // {
    // throw new UnsupportedOperationException("Custom socket implementation are not supported.");
    // }

    public ManagedDatagramSocket(SocketAddress bindaddr) throws SocketException
    {
        parentInstantiated = true;
        assureInit();
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
        assureInit();
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
        assureInit();
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
        if (!parentInstantiated)
        {
            return;
        }
        assureInit();
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

        ControlFlow cflow = cflowMgr.createControlFlow();
        SocketBindRequestEvent evt = createEvent(SocketBindRequestEvent.class, cflow);
        evt.setEndPoint(epoint);
        evt.setSocketType(SocketType.UDP);
        evt.setReusePort(reuseAddress);
        emitEvent(evt);
        ApplicationEvent responseEvent = cflow.awaitTermination();

        if (responseEvent instanceof SocketBoundEvent)
        {
            localAddress = ((SocketBoundEvent) responseEvent).getAddress();
            bound = true;
        }
        else
        {
            SocketErrorEvent error = (SocketErrorEvent) responseEvent;
            throw new SocketException("Failed to bind socket: " + error.getMessage());
        }
    }

    private <T extends ApplicationEvent> T createEvent(Class<T> eventClass, ControlFlow cflow)
    {
        EventFactory eventFactory = ManagedThread.currentThread().getEventFactory();
        return eventFactory.createEvent(eventClass, SimulatorBridge.getApplicationId(), cflow.getId());
    }

    private <T extends ApplicationEvent> T createEvent(Class<T> eventClass)
    {
        EventFactory eventFactory = ManagedThread.currentThread().getEventFactory();
        return eventFactory.createEvent(eventClass, SimulatorBridge.getApplicationId());
    }

    @Override
    public void connect(InetAddress address, int port)
    {
        assureInit();
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
        assureInit();
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
        assureInit();
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
        assureInit();
        return bound;
    }

    @Override
    public boolean isConnected()
    {
        assureInit();
        return connected;
    }

    @Override
    public InetAddress getInetAddress()
    {
        assureInit();
        return connectedAddress.getAddress();
    }

    @Override
    public int getPort()
    {
        assureInit();
        return connectedAddress.getPort();
    }

    @Override
    public SocketAddress getRemoteSocketAddress()
    {
        assureInit();
        if (!isConnected())
        {
            return null;
        }
        return new InetSocketAddress(getInetAddress(), getPort());
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        assureInit();
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
        assureInit();
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

            UDPPacketEvent event = createEvent(UDPPacketEvent.class);
            event.setSource(localAddress);
            event.setDestination((InetSocketAddress) p.getSocketAddress());
            event.setData(p.getData());
            event.setTimeToLive(255);
            emitEvent(event);
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
                        SimulatorBridge.getTimeline().waitOn(receiveBufferLock);
                    }
                    catch (InterruptedException e)
                    {
                        // TODO: review
                    }
                }
                DatagramPacket packet = receiveBuffer.remove(0);
                p.setSocketAddress(packet.getSocketAddress());
                int maxLen = Math.min(p.getData().length, packet.getData().length);
                for (int i = 0; i < maxLen; ++i)
                {
                    p.getData()[i] = packet.getData()[i];
                }
                p.setLength(maxLen);
            }
        }
    }

    @Override
    public InetAddress getLocalAddress()
    {
        assureInit();
        if (isClosed())
        {
            return null;
        }
        return localAddress.getAddress();
    }

    @Override
    public int getLocalPort()
    {
        assureInit();
        if (isClosed())
        {
            return -1;
        }
        return localAddress.getPort();
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.SO_TIMEOUT);
        event.setIntValue(timeout);
        emitEvent(event);

        socketTimeout = timeout;
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return socketTimeout;
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException
    {
        assureInit();
        if (!(size > 0))
        {
            throw new IllegalArgumentException("negative send size");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.SEND_BUFFER_SIZE);
        event.setIntValue(size);
        emitEvent(event);

        sendBufferSize = size;
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return sendBufferSize;
    }

    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException
    {
        assureInit();
        if (size <= 0)
        {
            throw new IllegalArgumentException("invalid receive size");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.RECEIVE_BUFFER_SIZE);
        event.setIntValue(size);
        emitEvent(event);

        receiveBufferSize = size;
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return receiveBufferSize;
    }

    @Override
    public synchronized void setReuseAddress(boolean on) throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.REUSE_ADDRESS);
        event.setBoolValue(on);
        emitEvent(event);

        reuseAddress = on;
    }

    @Override
    public synchronized boolean getReuseAddress() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return reuseAddress;
    }

    @Override
    public synchronized void setBroadcast(boolean on) throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.BROADCAST);
        event.setBoolValue(on);
        emitEvent(event);

        broadcast = on;
    }

    @Override
    public synchronized boolean getBroadcast() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return broadcast;
    }

    @Override
    public synchronized void setTrafficClass(int tc) throws SocketException
    {
        assureInit();
        if (tc < 0 || tc > 255)
        {
            throw new IllegalArgumentException("tc is not in range 0 -- 255");
        }

        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetDatagramSocketOptionEvent event = createEvent(SetDatagramSocketOptionEvent.class);
        event.setSocketAddres(localAddress);
        event.setOption(DatagramSocketOption.TRAFFIC_CLASS);
        event.setIntValue(tc);
        emitEvent(event);

        trafficClass = tc;
    }

    @Override
    public synchronized int getTrafficClass() throws SocketException
    {
        assureInit();
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return trafficClass;
    }

    @Override
    public void close()
    {
        assureInit();
        if (isClosed())
        {
            return;
        }

        SocketClosedEvent event = createEvent(SocketClosedEvent.class);
        emitEvent(event);
        cflowMgr.terminateAll(event);
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

    protected void emitEvent(ApplicationEvent evt)
    {
        assureInit();
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
    }

    protected InetSocketAddress getConnectedAddress()
    {
        assureInit();
        return connectedAddress;
    }

    // used to prevent double-binding
    private final boolean parentInstantiated;

    private boolean bound;
    private boolean closed;
    private InetSocketAddress connectedAddress;
    private InetSocketAddress localAddress;
    private boolean connected;
    private int socketTimeout;
    private int sendBufferSize;
    private int receiveBufferSize;
    private boolean reuseAddress;
    private boolean broadcast;
    private int trafficClass;
    private CFlowManager cflowMgr;
    private final Object receiveBufferLock = new Object();
    private final List<DatagramPacket> receiveBuffer = new ArrayList<DatagramPacket>();

    // default construction
    private void assureInit()
    {
        if (cflowMgr != null)
        {
            return;
        }
        cflowMgr = new CFlowManager(SimulatorBridge.getApplicationId(), SimulatorBridge.getEventBridge());
        cflowMgr.addHandler(UDPPacketEvent.class, new EventListener()
        {

            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                UDPPacketEvent data = (UDPPacketEvent) evt;

                // filtering by IP must be done in event handler
                if (data.getDestination().getPort() != localAddress.getPort())
                {
                    return;
                }

                synchronized (receiveBufferLock)
                {
                    try
                    {
                        receiveBuffer.add(new DatagramPacket(data.getData(), data.getData().length, data.getSource()));
                        SimulatorBridge.getTimeline().notifyAllOnTarget(receiveBufferLock);
                    }
                    catch (SocketException e)
                    {
                        // won't be the case
                    }
                }
            }
        });
    }
}
