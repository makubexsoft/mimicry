package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketAddress;

/**
 * Stub implementation of a {@link MulticastSocket} that translates all
 * interactions into events and vice-versa.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ManagedMulticastSocket extends MulticastSocket
{
	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedMulticastSocket() throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedMulticastSocket(int port) throws IOException
	{
		super(  );
		// TODO Auto-generated constructor stub
	}

	/**
	 * Overrides the original constructor that might be invoked by subclasses or
	 * via reflection. This implementation doesn't initialize the underlying
	 * socket and therefore doesn't perform any network communication.
	 */
	public ManagedMulticastSocket(SocketAddress bindaddr) throws IOException
	{
		super(  );
		// TODO Auto-generated constructor stub
	}

}
