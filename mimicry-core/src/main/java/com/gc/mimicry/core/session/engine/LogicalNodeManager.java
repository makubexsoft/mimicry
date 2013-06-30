package com.gc.mimicry.core.session.engine;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.messaging.MessagingSystem;
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
