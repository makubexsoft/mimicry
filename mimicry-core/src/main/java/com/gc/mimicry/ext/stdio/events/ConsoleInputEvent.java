package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.event.Event;

/**
 * This event represents input data to a application's stdin stream. Use this event to send input data to an
 * application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConsoleInputEvent extends Event
{

    public byte[] getData();

    public void setData(byte[] value);
}
