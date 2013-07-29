package com.gc.mimicry.engine.stack.events;

import com.gc.mimicry.engine.BaseEvent;

public class SetApplicationActiveEvent extends BaseEvent
{

    private boolean active;

    public boolean isActive()
    {
        return active;
    }

}
