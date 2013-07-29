package com.gc.mimicry.cluster;

import com.gc.mimicry.cluster.messaging.Message;
import com.gc.mimicry.util.model.ListBasedModel;

public class ClusterModel extends ListBasedModel<NodeInfo> implements ClusterListener
{

    public void nodeJoined(NodeInfo node)
    {
        if (!contains(node))
        {
            insert(node);
        }
    }

    public void nodeLeft(NodeInfo node)
    {
        int index = indexOf(node);
        if (index >= 0)
        {
            remove(index);
        }
    }

    public void messageReceived(Message msg, NodeInfo sender)
    {
    }
}
