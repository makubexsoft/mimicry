package com.gc.mimicry.engine.apps;

import java.util.UUID;

import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Preconditions;

public class LocalApplicationRef implements ApplicationRef
{
    private static final long serialVersionUID = 3540958629349184723L;
    private final Application app;
    private final UUID nodeId;
    private final UUID appId;

    public LocalApplicationRef(Application app)
    {
        Preconditions.checkNotNull(app);

        this.app = app;
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

    @Override
    public void start(String... commandArgs)
    {
        app.start(commandArgs);
    }

    @Override
    public Future<?> stop()
    {
        return app.stop();
    }
}
