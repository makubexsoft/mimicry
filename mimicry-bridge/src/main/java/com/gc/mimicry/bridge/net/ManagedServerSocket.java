package com.gc.mimicry.bridge.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ManagedServerSocket extends ServerSocket
{

	public ManagedServerSocket() throws IOException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ManagedServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException
	{
		super( port, backlog, bindAddr );
		// TODO Auto-generated constructor stub
	}

	public ManagedServerSocket(int port, int backlog) throws IOException
	{
		super( port, backlog );
		// TODO Auto-generated constructor stub
	}

	public ManagedServerSocket(int port) throws IOException
	{
		super( port );
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString()
	{
		return "Managed" + super.toString();
	}
}
