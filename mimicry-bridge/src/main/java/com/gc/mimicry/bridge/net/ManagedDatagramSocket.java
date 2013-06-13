package com.gc.mimicry.bridge.net;

import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class ManagedDatagramSocket extends DatagramSocket
{

	public ManagedDatagramSocket() throws SocketException
	{
		super();
		// TODO Auto-generated constructor stub
	}

	protected ManagedDatagramSocket(DatagramSocketImpl impl)
	{
		super( impl );
		// TODO Auto-generated constructor stub
	}

	public ManagedDatagramSocket(int port, InetAddress laddr) throws SocketException
	{
		super( port, laddr );
		// TODO Auto-generated constructor stub
	}

	public ManagedDatagramSocket(int port) throws SocketException
	{
		super( port );
		// TODO Auto-generated constructor stub
	}

	public ManagedDatagramSocket(SocketAddress bindaddr) throws SocketException
	{
		super( bindaddr );
		// TODO Auto-generated constructor stub
	}

}
