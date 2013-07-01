package com.gc.mimicry.shared.events;

import java.io.Serializable;
import java.util.UUID;

public interface Event extends Serializable
{
    /**
     * Returns the id of the corresponding control flow that is currently blocked or null if this is an asynchronous
     * event.
     * 
     * @return
     */
    public UUID getControlFlowId();

    public UUID getDestinationAppId();
}
