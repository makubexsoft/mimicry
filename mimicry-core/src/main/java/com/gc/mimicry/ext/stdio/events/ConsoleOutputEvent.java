package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.event.Event;

public interface ConsoleOutputEvent extends Event
{
    public byte[] getData();
}
