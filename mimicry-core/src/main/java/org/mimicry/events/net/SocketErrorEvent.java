package org.mimicry.events.net;

import org.mimicry.engine.ApplicationEvent;

public interface SocketErrorEvent extends ApplicationEvent
{
    public String getMessage();

    public void setMessage(String value);
}
