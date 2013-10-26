package org.mimicry.engine;

import org.mimicry.engine.stack.EventBridge;
import org.mimicry.engine.timing.Timeline;


public class ApplicationContext
{
    private ClassLoader classLoader;
    private EventBridge eventBridge;
    private Timeline timeline;

    public EventBridge getEventBridge()
    {
        return eventBridge;
    }

    public void setEventBridge(EventBridge eventBridge)
    {
        this.eventBridge = eventBridge;
    }

    public Timeline getTimeline()
    {
        return timeline;
    }

    public void setClock(Timeline timeline)
    {
        this.timeline = timeline;
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
