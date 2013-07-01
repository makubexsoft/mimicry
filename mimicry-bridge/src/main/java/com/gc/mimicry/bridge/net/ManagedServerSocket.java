package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.cflow.CFlowManager;
import com.gc.mimicry.bridge.cflow.ControlFlow;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.ServerSocketOption;
import com.gc.mimicry.shared.net.events.SetServerSocketOptionEvent;
import com.gc.mimicry.shared.net.events.SocketAcceptedEvent;
import com.gc.mimicry.shared.net.events.SocketAwaitingConnectionEvent;
import com.gc.mimicry.shared.net.events.SocketBindRequestEvent;
import com.gc.mimicry.shared.net.events.SocketBoundEvent;
import com.gc.mimicry.shared.net.events.SocketClosedEvent;
import com.gc.mimicry.shared.net.events.SocketErrorEvent;

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
            public void eventOccurred(Event evt)
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
     * Generates a {@link SocketAwaitingConnectionEvent} and awaits either an {@link SocketAcceptedEvent} in case there
     * is an incoming connection or a {@link SocketClosedEvent} if the socket is closed in the meanwhile.
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

        ControlFlow controlFlow = cflowMgr.createControlFlow();
        emitEvent(new SocketAwaitingConnectionEvent());
        controlFlow.awaitTermination();

        Event event = controlFlow.getTerminationCause();
        if (event instanceof SocketClosedEvent)
        {
            throw new SocketException("Socket has been closed.");
        }
        if (!(event instanceof SocketAcceptedEvent))
        {
            throw new RuntimeException("Received unexpected event: " + event);
        }

        SocketAcceptedEvent sae = (SocketAcceptedEvent) event;
        return new ManagedSocket(sae);
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

        ControlFlow controlFlow = cflowMgr.createControlFlow();

        SocketBindRequestEvent evt;
        evt = new SocketBindRequestEvent(SimulatorBridge.getApplicationId(), controlFlow.getId(), epoint.getPort(),
                reusePort);
        emitEvent(evt);

        controlFlow.awaitTermination();

        Event event = controlFlow.getTerminationCause();
        if (event instanceof SocketBoundEvent)
        {
            // STATE: bound = true
        }
        else
        {
            SocketErrorEvent error = (SocketErrorEvent) event;
            throw new SocketException("Failed to bind socket: " + error.getMessage());
        }
    }

    private void emitEvent(Event evt)
    {
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
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

        // TODO Auto-generated method stub
        return super.getLocalPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        if (!isBound())
        {
            return null;
        }

        // TODO Auto-generated method stub
        return super.getLocalSocketAddress();
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        int result = 0;
        // TODO Auto-generated method stub
        return super.getReceiveBufferSize();
    }

    @Override
    public boolean getReuseAddress() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        // TODO Auto-generated method stub
        return super.getReuseAddress();
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

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
    {
        // TODO Auto-generated method stub
        super.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

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
        // TODO Auto-generated method stub
        super.setReceiveBufferSize(size);
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        emitEvent(new SetServerSocketOptionEvent(ServerSocketOption.REUSE_ADDRESS, on));
        reusePort = on;
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        socketTimeout = timeout;
    }

    @Override
    public void close() throws IOException
    {
        // TODO Auto-generated method stub
        super.close();
    }

    @Override
    public String toString()
    {
        return "Managed" + super.toString();
    }

    private boolean bound;
    private boolean closed;
    private int socketTimeout;
    private boolean reusePort;
}
