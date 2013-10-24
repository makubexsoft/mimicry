package com.gc.mimicry.engine.event;

import java.util.UUID;

public interface EventFactory
{
    public <T extends ApplicationEvent> T createEvent(Class<T> eventClass, UUID applicationId);

    public <T extends ApplicationEvent> T createEvent(Class<T> eventClass, UUID applicationId, UUID controlFlow);

}
