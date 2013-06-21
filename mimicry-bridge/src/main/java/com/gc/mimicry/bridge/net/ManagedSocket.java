package com.gc.mimicry.bridge.net;

import java.io.IOException;
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
import com.gc.mimicry.core.cflow.ControlFlow;
import com.gc.mimicry.core.event.Event;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.event.EventListener;

/**
 * Stub implementation of the {@link Socket} that translates all interactions
 * into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedSocket extends Socket implements EventListener
{
	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket()
	{
		super();
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException
	{
		super();
		init( new InetSocketAddress( address, port ), new InetSocketAddress( localAddr, localPort ) );
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket(InetAddress address, int port) throws IOException
	{
		super();
		init( new InetSocketAddress( address, port ), null );
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket(Proxy proxy)
	{
		super();
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 * 
	 * This constructor type is currently not supported and throws an
	 * {@link UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException
	 *             Always.
	 */
	protected ManagedSocket(SocketImpl impl) throws SocketException
	{
		// TODO: currently custom socket implementations are not supported
		throw new UnsupportedOperationException( "currently custom socket implementations are not supported" );
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException
	{
		super();
		init( new InetSocketAddress( host, port ), new InetSocketAddress( localAddr, localPort ) );
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedSocket(String host, int port) throws UnknownHostException, IOException
	{
		super();
		init( new InetSocketAddress( host, port ), null );
	}

	private void init( InetSocketAddress address, InetSocketAddress localAddress ) throws IOException
	{
		if ( localAddress != null )
		{
			bind( localAddress );
			this.localAdress = localAddress;
		}
		if ( address != null )
		{
			connect( address );
			this.address = address;
		}
	}

	@Override
	public void bind( SocketAddress bindpoint ) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void close() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void connect( SocketAddress endpoint ) throws IOException
	{

		// TODO Auto-generated method stub

	}

	@Override
	public void connect( SocketAddress endpoint, int timeout ) throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String toString()
	{
		return "Managed" + super.toString();
	}

	@Override
	public void eventOccurred( Event evt )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setKeepAlive( boolean on ) throws SocketException
	{
		ControlFlow cflow = new ControlFlow();

		EventBridge bridge = SimulatorBridge.getEventBridge();
		// bridge.emit( new SetSocketOptionEvent( cflow.getId(),
		// SocketOption.KEEP_ALIVE, on ) );

		cflow.getFuture().awaitUninterruptibly( Long.MAX_VALUE );
		if ( cflow.getFuture().isSuccess() )
		{
			keepAlive = on;
		}
		else
		{
			//throw new IOException( "Failed to set socket option.", cflow.getFuture().getCause() );
		}
	}

	@Override
	public InetAddress getInetAddress()
	{
		if ( address == null )
		{
			return null;
		}
		return address.getAddress();
	}

	@Override
	public InetAddress getLocalAddress()
	{
		if ( localAdress == null )
		{
			return null;
		}
		return localAdress.getAddress();
	}

	@Override
	public int getLocalPort()
	{
		if ( localAdress == null )
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
		if ( address == null )
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
		throw new UnsupportedOperationException( "Currently channel-based sockets are not supported." );
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

	private InetSocketAddress	address;
	private InetSocketAddress	localAdress;
	private boolean				keepAlive;
	private boolean				oobInline;
	private int					receiveBufferSize;
	private boolean				reuseAddress;
	private int					sendBufferSize;
	private int					soLinger;
	private int					soTimeout;
	private boolean				tcpNoDelay;
	private int					trafficClass;
}
