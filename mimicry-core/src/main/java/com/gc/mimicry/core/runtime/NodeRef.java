package com.gc.mimicry.core.runtime;

import java.io.Serializable;
import java.util.UUID;

/**
 * Serializable node reference that is used to refer to nodes across JVMs.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface NodeRef extends Serializable
{
    /**
     * The id of the node.
     * 
     * @return
     */
    public UUID getNodeId();
}
