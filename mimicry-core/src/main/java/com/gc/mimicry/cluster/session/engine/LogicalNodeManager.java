package com.gc.mimicry.cluster.session.engine;

import com.gc.mimicry.cluster.messaging.MessagingSystem;
import com.gc.mimicry.util.BaseResourceManager;
import com.google.common.base.Preconditions;

public class LogicalNodeManager extends BaseResourceManager
{
    private final MessagingSystem messaging;

    public LogicalNodeManager(MessagingSystem messaging)
    {
        Preconditions.checkNotNull(messaging);
        this.messaging = messaging;
    }
}
