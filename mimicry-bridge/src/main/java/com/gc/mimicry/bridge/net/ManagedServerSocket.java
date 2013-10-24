package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;

import com.gc.mimicry.bridge.ControlFlow;
import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.cflow.CFlowManager;
import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.ApplicationEvent;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.ext.net.events.SocketBindRequestEvent;
import com.gc.mimicry.ext.net.events.SocketBoundEvent;
import com.gc.mimicry.ext.net.events.SocketClosedEvent;
import com.gc.mimicry.ext.net.events.SocketErrorEvent;
import com.gc.mimicry.ext.net.events.SocketType;
import com.gc.mimicry.ext.net.tcp.events.ConnectionEstablishedEvent;
import com.gc.mimicry.ext.net.tcp.events.ServerSocketOption;
import com.gc.mimicry.ext.net.tcp.events.SetPerformancePreferencesEvent;
import com.gc.mimicry.ext.net.tcp.events.SetServerSocketOptionEvent;
import com.gc.mimicry.ext.net.tcp.events.SocketAwaitingConnectionEvent;

/**
 * Stub implementation of the {@link ServerSocket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedServerSocket extends ServerSocket
{
    private static final int DEFAULT_BACKLOG = 50;
    private final CFlowManager cflowMgr = new CFlowManager(SimulatorBridge.getApplicationId(),
            SimulatorBridge.getEventBridge());

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedServerSocket() throws IOException
    {
        super();
        attachHandler();
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException
    {
        super();
        attachHandler();
        init(port, backlog, bindAddr);
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedServerSocket(int port, int backlog) throws IOException
    {
        super();
        attachHandler();
        init(port, backlog, null);
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedServerSocket(int port) throws IOException
    {
        super();
        attachHandler();
        init(port, DEFAULT_BACKLOG, null);
    }

    private void attachHandler()
    {
        cflowMgr.addHandler(SocketClosedEvent.class, new EventListener()
        {

            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                cflowMgr.terminateAll(evt);
            }

        });
    }

    private void init(int port, int backlog, InetAddress bindAddr) throws IOException
    {
        if (port < 0 || port > 0xFFFF)
        {
            throw new IllegalArgumentException("Port value out of range: " + port);
        }
        if (backlog < 1)
        {
            backlog = DEFAULT_BACKLOG;
        }
        try
        {
            bind(new InetSocketAddress(bindAddr, port), backlog);
        }
        catch (SecurityException e)
        {
            close();
            throw e;
        }
        catch (IOException e)
        {
            close();
            throw e;
        }
    }

    /**
     * Generates a {@link SocketAwaitingConnectionEvent} and awaits either an {@link ConnectionEstablishedEvent} in case
     * there is an incoming connection or a {@link SocketClosedEvent} if the socket is closed in the meanwhile.
     */
    @Override
    public Socket accept() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (!isBound())
        {
            throw new SocketException("Socket is not bound yet");
        }

        ControlFlow cflow = cflowMgr.createControlFlow();
        SocketAwaitingConnectionEvent event = createEvent(SocketAwaitingConnectionEvent.class, cflow);
        event.setLocalAddress(localAddress);
        emitEvent(event);
        ApplicationEvent responseEvent = cflow.awaitTermination();

        if (responseEvent instanceof SocketClosedEvent)
        {
            throw new SocketException("Socket has been closed.");
        }
        if (!(responseEvent instanceof ConnectionEstablishedEvent))
        {
            throw new RuntimeException("Received unexpected event: " + responseEvent);
        }

        ConnectionEstablishedEvent sae = (ConnectionEstablishedEvent) responseEvent;
        return new ManagedSocket(sae);
    }

    private void emitEvent(ApplicationEvent evt)
    {
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
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

    /**
     * Delegates to {@link #bind(SocketAddress, int)}
     */
    @Override
    public void bind(SocketAddress endpoint) throws IOException
    {
        bind(endpoint, DEFAULT_BACKLOG);
    }

    /**
     * Generates a {@link SocketBindRequestEvent} and awaits either an {@link SocketBoundEvent} or a
     * {@link SocketClosedEvent}.
     */
    @Override
    public void bind(SocketAddress endpoint, int backlog) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (isBound())
        {
            throw new SocketException("Already bound");
        }
        if (endpoint == null)
        {
            endpoint = new InetSocketAddress(0);
        }
        if (!(endpoint instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }
        InetSocketAddress epoint = (InetSocketAddress) endpoint;
        if (epoint.isUnresolved())
        {
            throw new SocketException("Unresolved address");
        }
        if (backlog < 1)
        {
            backlog = DEFAULT_BACKLOG;
        }

        ControlFlow cflow = cflowMgr.createControlFlow();
        SocketBindRequestEvent event = createEvent(SocketBindRequestEvent.class, cflow);
        event.setEndPoint(epoint);
        event.setSocketType(SocketType.TCP);
        event.setReusePort(reusePort);
        emitEvent(event);
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

    @Override
    public ServerSocketChannel getChannel()
    {
        return null;
    }

    @Override
    public InetAddress getInetAddress()
    {
        if (!isBound())
        {
            return null;
        }

        return super.getInetAddress();
    }

    @Override
    public int getLocalPort()
    {
        if (!isBound())
        {
            return -1;
        }

        return localAddress.getPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        if (!isBound())
        {
            return null;
        }

        return localAddress;
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
    public boolean getReuseAddress() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return reusePort;
    }

    @Override
    public synchronized int getSoTimeout() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return socketTimeout;
    }

    @Override
    public boolean isBound()
    {
        return bound;
    }

    @Override
    public boolean isClosed()
    {
        return closed;
    }

    /**
     * Emits a {@link SetPerformancePreferencesEvent}.
     */
    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
    {
        SetPerformancePreferencesEvent event = createEvent(SetPerformancePreferencesEvent.class);
        event.setSocketAddress(localAddress);
        event.setConnectionTime(connectionTime);
        event.setLatency(latency);
        event.setBandwidth(bandwidth);
        emitEvent(event);
    }

    /**
     * Emits an event of type {@link SetServerSocketOptionEvent} with {@link ServerSocketOption} = RECEIVE_BUFFER_SIZE
     */
    @Override
    public synchronized void setReceiveBufferSize(int size) throws SocketException
    {
        if (!(size > 0))
        {
            throw new IllegalArgumentException("negative receive size");
        }
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetServerSocketOptionEvent event = createEvent(SetServerSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(ServerSocketOption.RECEIVE_BUFFER_SIZE);
        event.setIntValue(size);
        emitEvent(event);

        receiveBufferSize = size;
    }

    /**
     * Emits an event of type {@link SetServerSocketOptionEvent} with {@link ServerSocketOption} = REUSE_ADDRESS
     */
    @Override
    public void setReuseAddress(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetServerSocketOptionEvent event = createEvent(SetServerSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(ServerSocketOption.REUSE_ADDRESS);
        event.setBoolValue(on);
        emitEvent(event);

        reusePort = on;
    }

    /**
     * Emits an event of type {@link SetServerSocketOptionEvent} with {@link ServerSocketOption} = SOCKET_TIMEOUT
     */
    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetServerSocketOptionEvent event = createEvent(SetServerSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(ServerSocketOption.SOCKET_TIMEOUT);
        event.setIntValue(timeout);
        emitEvent(event);

        socketTimeout = timeout;
    }

    @Override
    public void close() throws IOException
    {
        SocketClosedEvent event = createEvent(SocketClosedEvent.class);
        emitEvent(event);
        cflowMgr.terminateAll(event);
        closed = true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ManagedServerSocket [localAddress=");
        builder.append(localAddress);
        builder.append("]");
        return builder.toString();
    }

    private boolean bound;
    private boolean closed;
    private int socketTimeout;
    private boolean reusePort;
    private int receiveBufferSize;
    private InetSocketAddress localAddress;
}
