package com.gc.mimicry.bridge.weaving;

/**
 * The loop interception strategy is used to inject arbitary management code into loops.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface LoopInterceptionStrategy
{

    /**
     * Gets synchronously invoked on each loop iteration.
     */
    public void intercept();
}
