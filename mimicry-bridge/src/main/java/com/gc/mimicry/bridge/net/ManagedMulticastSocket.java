package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketAddress;

public class ManagedMulticastSocket extends MulticastSocket
{

	public ManagedMulticastSocket() throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ManagedMulticastSocket(int port) throws IOException
	{
		super( port );
		// TODO Auto-generated constructor stub
	}

	public ManagedMulticastSocket(SocketAddress bindaddr) throws IOException
	{
		super( bindaddr );
		// TODO Auto-generated constructor stub
	}

}
