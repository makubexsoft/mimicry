package com.gc.mimicry.engine;

import java.util.UUID;

import com.gc.mimicry.engine.local.LocalApplication;

public interface Node
{
    public LocalApplication installApplication(String bundleName, String path);

    public UUID getId();

    public String getName();
}
