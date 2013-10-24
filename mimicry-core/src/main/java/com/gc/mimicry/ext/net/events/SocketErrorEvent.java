package com.gc.mimicry.ext.net.events;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface SocketErrorEvent extends ApplicationEvent
{
    public String getMessage();

    public void setMessage(String value);
}
