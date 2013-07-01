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

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.cflow.ControlFlow;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.net.events.SocketAcceptedEvent;

/**
 * Stub implementation of the {@link Socket} that translates all interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedSocket extends Socket implements EventListener
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
            if (localAddr != null)
            {
                bind(localAddr);
            }
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
        // TODO Auto-generated method stub

    }

    @Override
    public synchronized void close() throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException
    {

        // TODO Auto-generated method stub

    }

    @Override
    public InputStream getInputStream() throws IOException
    {
        // TODO Auto-generated method stub
        return super.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException
    {
        // TODO Auto-generated method stub
        return super.getOutputStream();
    }

    @Override
    public boolean isBound()
    {
        // TODO Auto-generated method stub
        return super.isBound();
    }

    @Override
    public boolean isClosed()
    {
        // TODO Auto-generated method stub
        return super.isClosed();
    }

    @Override
    public boolean isConnected()
    {
        // TODO Auto-generated method stub
        return super.isConnected();
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

    @Override
    public void setOOBInline(boolean on) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setOOBInline(on);
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
        // TODO Auto-generated method stub
        super.setReceiveBufferSize(size);
    }

    @Override
    public void setReuseAddress(boolean on) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setReuseAddress(on);
    }

    @Override
    public synchronized void setSendBufferSize(int size) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setSendBufferSize(size);
    }

    @Override
    public void setSoLinger(boolean on, int linger) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setSoLinger(on, linger);
    }

    @Override
    public synchronized void setSoTimeout(int timeout) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setSoTimeout(timeout);
    }

    @Override
    public void setTcpNoDelay(boolean on) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setTcpNoDelay(on);
    }

    @Override
    public void setTrafficClass(int tc) throws SocketException
    {
        // TODO Auto-generated method stub
        super.setTrafficClass(tc);
    }

    @Override
    public void shutdownInput() throws IOException
    {
        // TODO Auto-generated method stub
        super.shutdownInput();
    }

    @Override
    public void shutdownOutput() throws IOException
    {
        // TODO Auto-generated method stub
        super.shutdownOutput();
    }

    public InetSocketAddress getAddress()
    {
        return address;
    }

    public InetSocketAddress getLocalAdress()
    {
        return localAdress;
    }

    public boolean isKeepAlive()
    {
        return keepAlive;
    }

    public boolean isOobInline()
    {
        return oobInline;
    }

    public boolean isReuseAddress()
    {
        return reuseAddress;
    }

    public boolean isTcpNoDelay()
    {
        return tcpNoDelay;
    }

    public void setAddress(InetSocketAddress address)
    {
        this.address = address;
    }

    public void setLocalAdress(InetSocketAddress localAdress)
    {
        this.localAdress = localAdress;
    }

    public void setOobInline(boolean oobInline)
    {
        this.oobInline = oobInline;
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString()
    {
        return "Managed" + super.toString();
    }

    @Override
    public void eventOccurred(Event evt)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setKeepAlive(boolean on) throws SocketException
    {
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
        if (address == null)
        {
            return null;
        }
        return address.getAddress();
    }

    @Override
    public InetAddress getLocalAddress()
    {
        if (localAdress == null)
        {
            return null;
        }
        return localAdress.getAddress();
    }

    @Override
    public int getLocalPort()
    {
        if (localAdress == null)
        {
            return -1;
        }
        return localAdress.getPort();
    }

    @Override
    public SocketAddress getLocalSocketAddress()
    {
        return localAdress;
    }

    @Override
    public int getPort()
    {
        if (address == null)
        {
            return 0;
        }
        return address.getPort();
    }

    @Override
    public SocketAddress getRemoteSocketAddress()
    {
        return address;
    }

    @Override
    public SocketChannel getChannel()
    {
        // TODO: Currently channel-based sockets are not supported.
        throw new UnsupportedOperationException("Currently channel-based sockets are not supported.");
    }

    @Override
    public boolean getKeepAlive() throws SocketException
    {
        return keepAlive;
    }

    @Override
    public boolean getOOBInline() throws SocketException
    {
        return oobInline;
    }

    @Override
    public synchronized int getReceiveBufferSize() throws SocketException
    {
        return receiveBufferSize;
    }

    @Override
    public boolean getReuseAddress() throws SocketException
    {
        return reuseAddress;
    }

    @Override
    public synchronized int getSendBufferSize() throws SocketException
    {
        return sendBufferSize;
    }

    @Override
    public int getSoLinger() throws SocketException
    {
        return soLinger;
    }

    @Override
    public synchronized int getSoTimeout() throws SocketException
    {
        return soTimeout;
    }

    @Override
    public boolean getTcpNoDelay() throws SocketException
    {
        return tcpNoDelay;
    }

    @Override
    public int getTrafficClass() throws SocketException
    {
        return trafficClass;
    }

    private InetSocketAddress address;
    private InetSocketAddress localAdress;
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
