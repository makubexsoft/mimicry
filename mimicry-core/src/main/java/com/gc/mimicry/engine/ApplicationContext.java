package com.gc.mimicry.engine;

import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Timeline;

public class ApplicationContext
{
    private ClassLoader classLoader;
    private EventBridge eventBridge;
    private Timeline clock;

    public EventBridge getEventBridge()
    {
        return eventBridge;
    }

    public void setEventBridge(EventBridge eventBridge)
    {
        this.eventBridge = eventBridge;
    }

    public Timeline getClock()
    {
        return clock;
    }

    public void setClock(Timeline clock)
    {
        this.clock = clock;
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    public void setClassLoader(ClassLoader loader)
    {
        classLoader = loader;
    }
}
