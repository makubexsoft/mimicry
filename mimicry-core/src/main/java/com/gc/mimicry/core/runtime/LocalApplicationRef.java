package com.gc.mimicry.core.runtime;

import java.util.UUID;

import com.google.common.base.Preconditions;

public class LocalApplicationRef implements ApplicationRef
{
    private final UUID nodeId;
    private final UUID appId;

    public LocalApplicationRef(Application app)
    {
        Preconditions.checkNotNull(app);

        nodeId = app.getNode().getId();
        appId = app.getId();
    }

    @Override
    public UUID getNodeId()
    {
        return nodeId;
    }

    @Override
    public UUID getApplicationId()
    {
        return appId;
    }

    @Override
    public String toString()
    {
        return "LocalApplicationRef [nodeId=" + nodeId + ", appId=" + appId + "]";
    }

}
