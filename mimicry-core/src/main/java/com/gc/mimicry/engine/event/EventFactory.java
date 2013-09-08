package com.gc.mimicry.engine.event;

import java.util.UUID;

public interface EventFactory
{
    public <T extends Event> T createEvent(Class<T> eventClass);

    public <T extends Event> T createEvent(Class<T> eventClass, UUID destinationApp);

    public <T extends Event> T createEvent(Class<T> eventClass, UUID sourceApp, UUID controlFlow);

    public <T extends Event> T createEvent(Class<T> eventClass, UUID sourceApp, UUID controlFlow, UUID destinationApp);
}
