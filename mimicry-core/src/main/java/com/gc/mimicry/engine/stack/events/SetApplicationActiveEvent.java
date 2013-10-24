package com.gc.mimicry.engine.stack.events;

import com.gc.mimicry.engine.event.ApplicationEvent;

public interface SetApplicationActiveEvent extends ApplicationEvent
{
    public boolean isActive();

    public void setActive(boolean value);
}
