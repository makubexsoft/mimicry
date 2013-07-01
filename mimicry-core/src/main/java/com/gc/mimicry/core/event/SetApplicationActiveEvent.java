package com.gc.mimicry.core.event;

import com.gc.mimicry.shared.events.BaseEvent;

public class SetApplicationActiveEvent extends BaseEvent
{

    private boolean active;

    public boolean isActive()
    {
        return active;
    }

}
