package com.gc.mimicry.engine.stack.events;

import com.gc.mimicry.engine.event.Event;

public interface SetApplicationActiveEvent extends Event
{
    public boolean isActive();

    public void setActive(boolean value);
}
