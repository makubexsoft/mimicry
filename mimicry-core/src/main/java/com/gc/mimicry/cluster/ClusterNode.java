package com.gc.mimicry.cluster;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.cluster.messaging.Message;

public interface ClusterNode extends Closeable
{

    public NodeInfo getNodeInfo();

    public void addClusterListener(ClusterListener l);

    public void removeClusterListener(ClusterListener l);

    public void close();

    public void broadcast(Message msg);

    public void send(Message msg, UUID destinationNode);
}
