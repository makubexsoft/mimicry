package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Stub implementation of the {@link ServerSocket} that translates all
 * interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedServerSocket extends ServerSocket
{
	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedServerSocket() throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedServerSocket(int port, int backlog) throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedServerSocket(int port) throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString()
	{
		return "Managed" + super.toString();
	}
}
