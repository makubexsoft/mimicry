package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.event.Event;

/**
 * This event represents data written to stdout of a certain application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConsoleOutputEvent extends Event
{
    public String getData();

    public void setData(String value);
}
