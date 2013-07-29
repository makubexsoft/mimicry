package com.gc.mimicry.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.gc.mimicry.cluster.ClusterListener;
import com.gc.mimicry.cluster.ClusterNode;
import com.gc.mimicry.cluster.NodeInfo;
import com.gc.mimicry.cluster.messaging.Message;

public class InMemoryNetwork
{

	private Map<UUID, VirtualNode>	nodes	= new HashMap<UUID, VirtualNode>();

	public VirtualNode createNode()
	{
		VirtualNode node = new VirtualNode();
		for ( VirtualNode n : nodes.values() )
		{
			n.notifyJoined( node.getNodeInfo() );
			node.notifyJoined( n.getNodeInfo() );
		}
		nodes.put( node.getNodeInfo().getNodeId(), node );
		return node;
	}

	public class VirtualNode implements ClusterNode, LocalNode
	{

		private Executor								executor	= Executors.newSingleThreadExecutor();
		private NodeInfo								nodeInfo	= new NodeInfo( 0, 0 );
		private CopyOnWriteArrayList<ClusterListener>	listener	= new CopyOnWriteArrayList<ClusterListener>();

		private VirtualNode()
		{
		}

		public void send( Message msg, UUID destinationNode )
		{
			VirtualNode node = nodes.get( destinationNode );
			if ( node != null )
			{
				node.deliver( msg, getNodeInfo() );
			}
		}

		public void removeClusterListener( ClusterListener l )
		{
			listener.remove( l );
		}

		public NodeInfo getNodeInfo()
		{
			return nodeInfo;
		}

		public void close()
		{
			nodes.remove( getNodeInfo().getNodeId() );
			for ( VirtualNode n : nodes.values() )
			{
				n.notifyRemove( getNodeInfo() );
			}
		}

		public void broadcast( Message msg )
		{
			for ( VirtualNode node : nodes.values() )
			{
				if ( !node.getNodeInfo().getNodeId().equals( getNodeInfo().getNodeId() ) )
				{
					node.deliver( msg, getNodeInfo() );
				}
			}
		}

		public void notifyRemove( final NodeInfo info )
		{
			executor.execute( new Runnable()
			{

				public void run()
				{
					for ( ClusterListener l : listener )
					{
						l.nodeLeft( info );
					}
				}
			} );
		}

		public void notifyJoined( final NodeInfo info )
		{
			executor.execute( new Runnable()
			{

				public void run()
				{
					for ( ClusterListener l : listener )
					{
						l.nodeJoined( info );
					}
				}
			} );
		}

		public void addClusterListener( ClusterListener l )
		{
			listener.add( l );
		}

		public void deliver( final Message m, final NodeInfo source )
		{
			executor.execute( new Runnable()
			{

				public void run()
				{
					for ( ClusterListener l : listener )
					{
						l.messageReceived( m, source );
					}
				}
			} );
		}
	}
}
