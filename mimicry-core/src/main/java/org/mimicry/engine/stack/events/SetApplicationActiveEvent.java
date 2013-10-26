package org.mimicry.engine.stack.events;

import org.mimicry.engine.event.ApplicationEvent;

public interface SetApplicationActiveEvent extends ApplicationEvent
{
    public boolean isActive();

    public void setActive(boolean value);
}
