package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface ConsoleOutputEvent extends ApplicationEvent
{
    public byte[] getData();
}
