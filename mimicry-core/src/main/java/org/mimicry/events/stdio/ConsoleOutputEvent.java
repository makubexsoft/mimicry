package org.mimicry.events.stdio;

import org.mimicry.engine.ApplicationEvent;

public interface ConsoleOutputEvent extends ApplicationEvent
{
    public byte[] getData();
}
