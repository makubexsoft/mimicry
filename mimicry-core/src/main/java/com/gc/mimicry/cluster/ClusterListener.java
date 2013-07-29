package com.gc.mimicry.cluster;

import com.gc.mimicry.cluster.messaging.Message;

public interface ClusterListener
{
    public void nodeJoined(NodeInfo node);

    public void nodeLeft(NodeInfo node);

    public void messageReceived(Message msg, NodeInfo sender);
}
