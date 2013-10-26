package org.mimicry.events;

import org.mimicry.engine.ApplicationEvent;

public interface SetApplicationActiveEvent extends ApplicationEvent
{
    public boolean isActive();

    public void setActive(boolean value);
}
