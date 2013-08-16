package com.gc.mimicry.ext.stdio.events;

import com.gc.mimicry.engine.BaseEvent;

/**
 * This event represents data written to stdout of a certain application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ConsoleOutputEvent extends BaseEvent
{
    private static final long serialVersionUID = -6312747858804388533L;
    private final String data;

    public ConsoleOutputEvent(String data)
    {
        this.data = data;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ConsoleOutputEvent [data='");
        builder.append(data);
        builder.append("']");
        return builder.toString();
    }

}
