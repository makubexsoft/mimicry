package com.gc.mimicry.core.timing;

/**
 * The type of supported clocks.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public enum ClockType
{
    /**
     * A real-time clock that advances automatically over time but multiplied by a certain multiplier. This clock can be
     * started and stopped.
     */
    REALTIME,
    /**
     * A discrete clock that does not advance by itself over time. Instead you must manually advance the time by a given
     * amount of milliseconds.
     */
    DISCRETE
}
