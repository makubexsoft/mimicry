package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.event.Event;

/**
 * This event represents data written to stdout of a certain application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConsoleStdoutEvent extends Event, ConsoleOutputEvent
{
    @Override
    public byte[] getData();

    public void setData(byte[] value);
}
