package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.UnknownHostException;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.core.event.Event;
import com.gc.mimicry.core.event.EventBridge;
import com.gc.mimicry.core.event.EventListener;

public class ManagedSocket extends Socket implements EventListener
{

	private EventBridge	bridge;
	private Proxy		proxy	= Proxy.NO_PROXY;

	@Override
	public void eventReceived( Event evt )
	{
		// TODO Auto-generated method stub

	}

	public ManagedSocket()
	{
		super();
		bridge = SimulatorBridge.getEventBridge();
	}

	public ManagedSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException
	{
		super( address, port, localAddr, localPort );
	}

	public ManagedSocket(InetAddress address, int port) throws IOException
	{
		super( address, port );
	}

	public ManagedSocket(Proxy proxy)
	{
		super( proxy );
		this.proxy = proxy;
	}

	protected ManagedSocket(SocketImpl impl) throws SocketException
	{
		super( impl );
	}

	public ManagedSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException
	{
		super( host, port, localAddr, localPort );
	}

	public ManagedSocket(String host, int port) throws UnknownHostException, IOException
	{
		super( host, port );
	}

	@Override
	public void bind( SocketAddress bindpoint ) throws IOException
	{
		// TODO Auto-generated method stub
		super.bind( bindpoint );
	}

	@Override
	public synchronized void close() throws IOException
	{
		// TODO Auto-generated method stub
		super.close();
	}

	@Override
	public void connect( SocketAddress endpoint ) throws IOException
	{

		// TODO Auto-generated method stub
		super.connect( endpoint );
	}

	@Override
	public void connect( SocketAddress endpoint, int timeout ) throws IOException
	{
		// TODO Auto-generated method stub
		super.connect( endpoint, timeout );
	}

	@Override
	public String toString()
	{
		return "Managed" + super.toString();
	}
}
