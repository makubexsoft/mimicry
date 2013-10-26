package org.mimicry.bridge.net;

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
import java.util.Arrays;

import org.mimicry.EventListener;
import org.mimicry.bridge.ControlFlow;
import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.cflow.CFlowManager;
import org.mimicry.bridge.threading.ManagedThread;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventFactory;
import org.mimicry.events.net.SocketBindRequestEvent;
import org.mimicry.events.net.SocketBoundEvent;
import org.mimicry.events.net.SocketErrorEvent;
import org.mimicry.events.net.SocketType;
import org.mimicry.events.net.tcp.ConnectionEstablishedEvent;
import org.mimicry.events.net.tcp.SetPerformancePreferencesEvent;
import org.mimicry.events.net.tcp.SetSocketOptionEvent;
import org.mimicry.events.net.tcp.SocketConnectionRequest;
import org.mimicry.events.net.tcp.SocketOption;
import org.mimicry.events.net.tcp.TCPReceivedDataEvent;
import org.mimicry.events.net.tcp.TCPSendDataEvent;
import org.mimicry.util.ByteBuffer;


/**
 * Stub implementation of the {@link Socket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedSocket extends Socket
{

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
    ManagedSocket(ConnectionEstablishedEvent event)
    {
        localAddress = event.getServerAddress();
        remoteAddress = event.getClientAddress();

        closed = false;
        connected = true;
        bound = true;
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
        SocketBindRequestEvent evt = createEvent(SocketBindRequestEvent.class, cflow);
        evt.setEndPoint(epoint);
        evt.setSocketType(SocketType.TCP);
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

        return receiveBuffer.createStream();
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

        return out;
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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.OOB_INLINE);
        event.setBoolValue(on);
        emitEvent(event);

        oobInline = on;
    }

    @Override
    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth)
    {
        SetPerformancePreferencesEvent event = createEvent(SetPerformancePreferencesEvent.class);
        event.setSocketAddress(localAddress);
        event.setConnectionTime(connectionTime);
        event.setLatency(latency);
        event.setBandwidth(bandwidth);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.RECEIVE_BUFFER_SIZE);
        event.setIntValue(size);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.REUSE_ADDRESS);
        event.setBoolValue(on);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.SEND_BUFFER_SIZE);
        event.setIntValue(size);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.SO_LINGER);
        event.setIntValue(linger);
        event.setBoolValue(on);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.SO_TIMEOUT);
        event.setIntValue(timeout);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.TCP_NO_DELAY);
        event.setBoolValue(on);
        emitEvent(event);

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

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.TRAFFIC_CLASS);
        event.setIntValue(tc);
        emitEvent(event);

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
     * Emits a {@link SocketConnectionRequest} and awaits either a {@link ConnectionEstablishedEvent} or a
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
        SocketConnectionRequest evt = createEvent(SocketConnectionRequest.class, cflow);
        evt.setSource(localAddress);
        evt.setDestination(epoint);
        emitEvent(evt);
        ApplicationEvent responseEvent = cflow.awaitTermination();

        if (responseEvent instanceof ConnectionEstablishedEvent)
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
            SocketErrorEvent error = (SocketErrorEvent) responseEvent;
            throw new SocketException(error.getMessage());
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ManagedSocket [remoteAddress=");
        builder.append(remoteAddress);
        builder.append(", localAddress=");
        builder.append(localAddress);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException
    {
        if (isClosed())
        {
            throw new SocketException("Socket is closed");
        }

        SetSocketOptionEvent event = createEvent(SetSocketOptionEvent.class);
        event.setSocketAddress(localAddress);
        event.setOption(SocketOption.KEEP_ALIVE);
        event.setBoolValue(on);
        emitEvent(event);

        keepAlive = on;
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
        // This is for backward compatibility
        if (!isBound())
        {
            return null;
            // FIXME: only available in JDK 7 return InetAddress.anyLocalAddress();
        }
        return localAddress.getAddress();
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

    private void emitEvent(ApplicationEvent evt)
    {
        SimulatorBridge.getEventBridge().dispatchEventToStack(evt);
    }

    private final CFlowManager cflowMgr;
    private final MSOutputStream out;
    private final ByteBuffer receiveBuffer;
    private boolean bound;
    private boolean connected;
    private boolean closed;
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

    // default construction
    {
        cflowMgr = new CFlowManager(SimulatorBridge.getApplicationId(), SimulatorBridge.getEventBridge());
        cflowMgr.addHandler(TCPReceivedDataEvent.class, new EventListener()
        {

            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                TCPReceivedDataEvent data = (TCPReceivedDataEvent) evt;

                // filtering by IP must be done in event handler
                if (data.getDestinationSocket().getPort() == localAddress.getPort())
                {
                    receiveBuffer.write(data.getData());
                }
            }
        });
        out = new MSOutputStream();
        receiveBuffer = new ByteBuffer();
    }

    private class MSOutputStream extends OutputStream
    {
        @Override
        public void write(int b) throws IOException
        {
            byte[] buffer = new byte[] { (byte) b };
            write(buffer, 0, 1);
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException
        {
            byte[] buffer = Arrays.copyOfRange(b, off, off + len);

            TCPSendDataEvent event = createEvent(TCPSendDataEvent.class);
            event.setSourceSocket(localAddress);
            event.setDestinationSocket(remoteAddress);
            event.setData(buffer);
            emitEvent(event);
        }
    }
}
