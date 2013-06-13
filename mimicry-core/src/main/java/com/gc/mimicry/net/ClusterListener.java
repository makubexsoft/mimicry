package com.gc.mimicry.net;

import com.gc.mimicry.core.messaging.Message;

public interface ClusterListener
{
	public void nodeJoined( NodeInfo node );

	public void nodeLeft( NodeInfo node );

	public void messageReceived( Message msg, NodeInfo sender );
}
