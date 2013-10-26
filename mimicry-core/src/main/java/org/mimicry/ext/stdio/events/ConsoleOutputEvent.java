package org.mimicry.ext.stdio.events;

import org.mimicry.engine.event.ApplicationEvent;

public interface ConsoleOutputEvent extends ApplicationEvent
{
    public byte[] getData();
}
