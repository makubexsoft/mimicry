package org.mimicry.events.stdio;

import org.mimicry.engine.ApplicationEvent;

/**
 * This event represents input data to a application's stdin stream. Use this event to send input data to an
 * application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ConsoleStdinEvent extends ApplicationEvent, ConsoleOutputEvent
{

    public byte[] getData();

    public void setData(byte[] value);
}
