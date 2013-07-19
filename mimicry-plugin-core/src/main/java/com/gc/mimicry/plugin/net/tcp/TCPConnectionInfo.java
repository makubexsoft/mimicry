package com.gc.mimicry.plugin.net.tcp;

import java.net.InetSocketAddress;

public class TCPConnectionInfo
{
	private final InetSocketAddress	clientAddress;
	private final InetSocketAddress	serverAddress;

	public TCPConnectionInfo(InetSocketAddress clientAddress, InetSocketAddress serverAddress)
	{
		this.clientAddress = clientAddress;
		this.serverAddress = serverAddress;
	}

	public InetSocketAddress getClientAddress()
	{
		return clientAddress;
	}

	public InetSocketAddress getServerAddress()
	{
		return serverAddress;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientAddress == null) ? 0 : clientAddress.hashCode());
		result = prime * result + ((serverAddress == null) ? 0 : serverAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
		{
			return true;
		}
		if ( obj == null )
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}
		TCPConnectionInfo other = (TCPConnectionInfo) obj;
		if ( clientAddress == null )
		{
			if ( other.clientAddress != null )
			{
				return false;
			}
		}
		else if ( !clientAddress.equals( other.clientAddress ) )
		{
			return false;
		}
		if ( serverAddress == null )
		{
			if ( other.serverAddress != null )
			{
				return false;
			}
		}
		else if ( !serverAddress.equals( other.serverAddress ) )
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "TCPConnectionInfo [clientAddress=" );
		builder.append( clientAddress );
		builder.append( ", serverAddress=" );
		builder.append( serverAddress );
		builder.append( "]" );
		return builder.toString();
	}
}
