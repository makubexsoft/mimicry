package org.mimicry;

import java.util.UUID;

public interface Node
{
    public Application installApplication(String bundleName, String path);

    public UUID getId();

    public String getName();

    /**
     * Returns information on the engine that hosts this node.
     * 
     * @return
     */
    public EngineInfo getEngineInfo();
}
