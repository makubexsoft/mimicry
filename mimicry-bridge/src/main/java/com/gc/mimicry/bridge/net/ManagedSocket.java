package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import com.gc.mimicry.bridge.Bridge;
import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.cflow.CFlowManager;
import com.gc.mimicry.bridge.cflow.ControlFlow;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.SetSocketOptionEvent;
import com.gc.mimicry.shared.net.events.SocketAcceptedEvent;
import com.gc.mimicry.shared.net.events.SocketBindRequestEvent;
import com.gc.mimicry.shared.net.events.SocketBoundEvent;
import com.gc.mimicry.shared.net.events.SocketConnectionRequest;
import com.gc.mimicry.shared.net.events.SocketErrorEvent;
import com.gc.mimicry.shared.net.events.SocketOption;

/**
 * Stub implementation of the {@link Socket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedSocket extends Socket
{
    private final CFlowManager cflowMgr = new CFlowManager(Bridge.appId(), SimulatorBridge.getEventBridge());
    private boolean bound;
    private boolean connected;
    private boolean closed;

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket()
    {
        super();
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException
    {
        super();
        init(address != null ? new InetSocketAddress(address, port) : null,
                new InetSocketAddress(localAddr, localPort), true);
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket(InetAddress address, int port) throws IOException
    {
        super();
        init(address != null ? new InetSocketAddress(address, port) : null, (SocketAddress) null, true);
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket(Proxy proxy)
    {
        super();
        if (proxy == null)
        {
            throw new IllegalArgumentException("Invalid Proxy");
        }
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     * 
     * This constructor type is currently not supported and throws an {@link UnsupportedOperationException}.
     * 
     * @throws UnsupportedOperationException
     *             Always.
     */
    protected ManagedSocket(SocketImpl impl) throws SocketException
    {
        // TODO: currently custom socket implementations are not supported
        throw new UnsupportedOperationException("currently custom socket implementations are not supported");
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException
    {
        super();
        init(host != null ? new InetSocketAddress(host, port)
                : new InetSocketAddress(InetAddress.getByName(null), port),
                new InetSocketAddress(localAddr, localPort), true);
    }

    /**
     * Overrides the original constructor that might be invoked by subclasses or via reflection. This implementation
     * doesn't initialize the underlying socket and therefore doesn't perform any network communication.
     */
    public ManagedSocket(String host, int port) throws UnknownHostException, IOException
    {
        super();
        init(host != null ? new InetSocketAddress(host, port)
                : new InetSocketAddress(InetAddress.getByName(null), port), (SocketAddress) null, true);
    }

    @Deprecated
    public ManagedSocket(String host, int port, boolean stream) throws IOException
    {
        super();
        init(host != null ? new InetSocketAddress(host, port)
                : new InetSocketAddress(InetAddress.getByName(null), port), (SocketAddress) null, stream);
    }

    @Deprecated
    public ManagedSocket(InetAddress host, int port, boolean stream) throws IOException
    {
        init(host != null ? new InetSocketAddress(host, port) : null, new InetSocketAddress(0), stream);
    }

    /**
     * Invoked when this socket results from the {@link ManagedServerSocket#accept()} operation.
     * 
     * @param event
     */
    ManagedSocket(SocketAcceptedEvent event)
    {

    }

    private void init(SocketAddress address, SocketAddress localAddr, boolean stream) throws IOException
    {
        if (!stream)
        {
            throw new UnsupportedOperationException("Non-stream implementations are not supported");
        }
        // backward compatibility
        if (address == null)
        {
            throw new NullPointerException();
        }

        try
        {
            if (localAddr == null)
            {
                localAddr = new InetSocketAddress(0);
            }
            bind(localAddr);
            if (address != null)
            {
                connect(address);
            }
        }
        catch (IOException e)
        {
            close();
            throw e;
        }
    }

    @Override
    public void bind(SocketAddress bindpoint) throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (isBound())
        {
            throw new SocketException("Already bound");
        }

        if (bindpoint != null && (!(bindpoint instanceof InetSocketAddress)))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }
        InetSocketAddress epoint = (InetSocketAddress) bindpoint;
        if (epoint != null && epoint.isUnresolved())
        {
            throw new SocketException("Unresolved address");
        }
        if (epoint == null)
        {
            epoint = new InetSocketAddress(0);
        }

        ControlFlow cflow = cflowMgr.createControlFlow();
        Bridge.emitEvent(new SocketBindRequestEvent(Bridge.appId(), cflow.getId(), epoint, reuseAddress));
        cflow.awaitTermination();

        Event event = cflow.getTerminationCause();
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
    public synchronized void close() throws IOException
    {
        // TODO Auto-generated method stub

    }

    /**
     * Delegates to {@link #connect(SocketAddress, int)}.
     */
    @Override
    public void connect(SocketAddress endpoint) throws IOException
    {
        connect(endpoint, 0);
    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected())
        {
            throw new SocketException("Socket is not connected");
        }
        if (isInputShutdown())
        {
            throw new SocketException("Socket input is shutdown");
        }

        // TODO:
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected())
        {
            throw new SocketException("Socket is not connected");
        }
        if (isOutputShutdown())
        {
            throw new SocketException("Socket output is shutdown");
        }

        // TODO:
        return null;
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
    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public boolean isInputShutdown()
    {
        // TODO Auto-generated method stub
        return super.isInputShutdown();
    }

    @Override
    public boolean isOutputShutdown()
    {
        // TODO Auto-generated method stub
        return super.isOutputShutdown();
    }

    @Override
    public void sendUrgentData(int data) throws IOException
    {
        // TODO Auto-generated method stub
        super.sendUrgentData(data);
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = OOB_INLINE
     */
    @Override
    public void setOOBInline(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.OOB_INLINE, on));
        oobInline = on;
    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
    {
        // TODO Auto-generated method stub
        super.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = RECEIVE_BUFFER_SIZE
     */
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

        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.RECEIVE_BUFFER_SIZE, size));
        receiveBufferSize = size;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = REUSE_ADDRESS
     */
    @Override
    public void setReuseAddress(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.REUSE_ADDRESS, on));
        reuseAddress = on;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = SEND_BUFFER_SIZE
     */
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

        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.SEND_BUFFER_SIZE, size));
        sendBufferSize = size;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = SO_LINGER
     */
    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (on)
        {
            if (linger < 0)
            {
                throw new IllegalArgumentException("invalid value for SO_LINGER");
            }
            if (linger > 65535)
            {
                linger = 65535;
            }

        }
        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.SO_LINGER, linger, on));
        soLinger = linger;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = SO_TIMEOUT
     */
    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (timeout < 0)
        {
            throw new IllegalArgumentException("timeout can't be negative");
        }

        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.SO_TIMEOUT, timeout));
        soTimeout = timeout;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = TCP_NO_DELAY
     */
    @Override
    public void setTcpNoDelay(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.TCP_NO_DELAY, on));
        tcpNoDelay = on;
    }

    /**
     * Emits event of type {@link SetSocketOptionEvent} with {@link SocketOption} = TRAFFIC_CLASS
     */
    @Override
    public void setTrafficClass(int tc) throws SocketException
    {
        if (tc < 0 || tc > 255)
        {
            throw new IllegalArgumentException("tc is not in range 0 -- 255");
        }

        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        Bridge.emitEvent(new SetSocketOptionEvent(Bridge.appId(), SocketOption.TRAFFIC_CLASS, tc));
        trafficClass = tc;
    }

    @Override
    public void shutdownInput() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected())
        {
            throw new SocketException("Socket is not connected");
        }
        if (isInputShutdown())
        {
            throw new SocketException("Socket input is already shutdown");
        }
        // TODO Auto-generated method stub
        super.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected())
        {
            throw new SocketException("Socket is not connected");
        }
        if (isOutputShutdown())
        {
            throw new SocketException("Socket output is already shutdown");
        }
        // TODO Auto-generated method stub
        super.shutdownOutput();
    }

    /**
     * Emits a {@link SocketConnectionRequest} and awaits either a {@link SocketAcceptedEvent} or a
     * {@link SocketErrorEvent}.
     */
    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException
    {
        if (endpoint == null)
        {
            throw new IllegalArgumentException("connect: The address can't be null");
        }

        if (timeout < 0)
        {
            throw new IllegalArgumentException("connect: timeout can't be negative");
        }

        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        if (isConnected())
        {
            throw new SocketException("already connected");
        }

        if (!(endpoint instanceof InetSocketAddress))
        {
            throw new IllegalArgumentException("Unsupported address type");
        }

        InetSocketAddress epoint = (InetSocketAddress) endpoint;

        ControlFlow cflow = cflowMgr.createControlFlow();

        Bridge.emitEvent(new SocketConnectionRequest(Bridge.appId(), cflow.getId(), localAddress, epoint));

        cflow.awaitTermination();

        Event cause = cflow.getTerminationCause();
        if (cause instanceof SocketAcceptedEvent)
        {
            remoteAddress = epoint;
            connected = true;
            /*
             * If the socket was not bound before the connect, it is now because the kernel will have picked an
             * ephemeral port & a local address
             */
            bound = true;
        }
        else
        {
            SocketErrorEvent error = (SocketErrorEvent) cause;
            throw new SocketException(error.getMessage());
        }
    }

    @Override
    public String toString()
    {
        return "Managed" + super.toString();
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        ControlFlow cflow = new ControlFlow();

        EventBridge bridge = SimulatorBridge.getEventBridge();
        // bridge.emit( new SetSocketOptionEvent( cflow.getId(),
        // SocketOption.KEEP_ALIVE, on ) );

        // cflow.getFuture().awaitUninterruptibly(Long.MAX_VALUE);
        // if (cflow.getFuture().isSuccess())
        // {
        // keepAlive = on;
        // }
        // else
        // {
        // // throw new IOException( "Failed to set socket option.", cflow.getFuture().getCause() );
        // }
    }

    @Override
    public InetAddress getInetAddress()
    {
        if (remoteAddress == null)
        {
            return null;
        }
        return remoteAddress.getAddress();
    }

    @Override
    public InetAddress getLocalAddress()
    {
        // // This is for backward compatibility
        // if (!isBound())
        // {
        // return InetAddress.anyLocalAddress();
        // }
        // InetAddress in = null;
        // try
        // {
        // in = (InetAddress) getImpl().getOption(SocketOptions.SO_BINDADDR);
        // if (in.isAnyLocalAddress())
        // {
        // in = InetAddress.anyLocalAddress();
        // }
        // }
        // catch (Exception e)
        // {
        // in = InetAddress.anyLocalAddress(); // "0.0.0.0"
        // }
        // return localAdress.getAddress();
        return null;
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
        return new InetSocketAddress(getLocalAddress(), getLocalPort());
    }

    @Override
    public int getPort()
    {
        if (!isConnected())
        {
            return 0;
        }
        return remoteAddress.getPort();
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
    public SocketChannel getChannel()
    {
        return null;
    }

    @Override
    public boolean getKeepAlive() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return keepAlive;
    }

    @Override
    public boolean getOOBInline() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return oobInline;
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
        return reuseAddress;
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
    public int getSoLinger() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return soLinger;
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        return soTimeout;
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }
        return tcpNoDelay;
    }

    @Override
    public int getTrafficClass() throws SocketException
    {
        return trafficClass;
    }

    private InetSocketAddress remoteAddress;
    private InetSocketAddress localAddress;
    private boolean keepAlive;
    private boolean oobInline;
    private int receiveBufferSize;
    private boolean reuseAddress;
    private int sendBufferSize;
    private int soLinger;
    private int soTimeout;
    private boolean tcpNoDelay;
    private int trafficClass;
}
