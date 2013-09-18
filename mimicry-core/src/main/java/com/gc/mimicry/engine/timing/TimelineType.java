package com.gc.mimicry.engine.timing;

/**
 * The type of supported time lines.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public enum TimelineType
{
    /**
     * A real-time time line that advances automatically over time but multiplied by a certain multiplier. This time
     * line can be started and stopped.
     */
    REALTIME,
    /**
     * A discrete time line that does not advance by itself over time. Instead you must manually advance the time by a
     * given amount of milliseconds.
     */
    DISCRETE,
    /**
     * Use the system's time line which is the original one of the JVM.
     */
    SYSTEM
}
