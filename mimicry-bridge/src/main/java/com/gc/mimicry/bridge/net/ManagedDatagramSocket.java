package com.gc.mimicry.bridge.net;

import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Stub implementation of a {@link DatagramSocket} that translates all
 * interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedDatagramSocket extends DatagramSocket
{
	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 * 
	 * @throws SocketException
	 */
	public ManagedDatagramSocket() throws SocketException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	protected ManagedDatagramSocket(DatagramSocketImpl impl)
	{
		super( impl );
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedDatagramSocket(int port, InetAddress laddr) throws SocketException
	{
		super( port, laddr );
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedDatagramSocket(int port) throws SocketException
	{
		super( port );
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedDatagramSocket(SocketAddress bindaddr) throws SocketException
	{
		super( bindaddr );
		// TODO Auto-generated constructor stub
	}

}
