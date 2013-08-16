package com.gc.mimicry.cluster.session;

import java.util.UUID;

import com.gc.mimicry.cluster.messaging.Message;

public class QuerySessionRequest extends Message
{

    private static final long serialVersionUID = 521591002864853517L;
    private final UUID requestId;

    public QuerySessionRequest()
    {
        requestId = UUID.randomUUID();
    }

    public UUID getRequestId()
    {
        return requestId;
    }
}