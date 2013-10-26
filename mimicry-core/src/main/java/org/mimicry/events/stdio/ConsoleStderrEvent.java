package org.mimicry.events.stdio;

import org.mimicry.engine.ApplicationEvent;

/**
 * This event represents data written to stderr of a certain application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConsoleStderrEvent extends ApplicationEvent, ConsoleOutputEvent
{
    @Override
    public byte[] getData();

    public void setData(byte[] value);
}
