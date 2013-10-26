package org.mimicry.ext.net.events;

import org.mimicry.engine.event.ApplicationEvent;

public interface SocketErrorEvent extends ApplicationEvent
{
    public String getMessage();

    public void setMessage(String value);
}
