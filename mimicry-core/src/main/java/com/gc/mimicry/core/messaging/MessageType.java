package com.gc.mimicry.core.messaging;

import com.gc.mimicry.core.event.EventHandler;

/**
 * Messages are categorized according to their importance and size.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public enum MessageType
{
    /**
     * Control messages have the highest processing priority and are used to control and manage the life-cycle of the
     * simulator.
     */
    CONTROL,
    /**
     * Data messages may contain large amounts of binary data. They are transmitted using a separate channel to avoid
     * message delivery delays of {@link #CONTROL} and {@link #CUSTOM}.
     */
    DATA,
    /**
     * Custom messages are typically created by {@link EventHandler}s and should be small to avoid large network delays.
     */
    CUSTOM
}
