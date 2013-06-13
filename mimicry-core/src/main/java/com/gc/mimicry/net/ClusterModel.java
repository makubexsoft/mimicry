package com.gc.mimicry.net;

import com.gc.mimicry.core.messaging.Message;
import com.gc.mimicry.core.model.ListBasedModel;

public class ClusterModel extends ListBasedModel<NodeInfo> implements ClusterListener
{

	public void nodeJoined( NodeInfo node )
	{
		if ( !contains( node ) )
		{
			insert( node );
		}
	}

	public void nodeLeft( NodeInfo node )
	{
		int index = indexOf( node );
		if ( index >= 0 )
		{
			remove( index );
		}
	}

	public void messageReceived( Message msg, NodeInfo sender )
	{
	}
}
