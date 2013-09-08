package com.gc.mimicry.ext.net.events;

import com.gc.mimicry.engine.event.Event;

public interface SocketErrorEvent extends Event
{
    public String getMessage();

    public void setMessage(String value);
}
