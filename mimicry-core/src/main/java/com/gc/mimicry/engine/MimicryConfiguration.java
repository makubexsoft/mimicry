package com.gc.mimicry.engine;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MimicryConfiguration
{
    public MimicryConfiguration(ClassLoader eventHandlerClassLoader)
    {
        this.eventHandlerClassLoader = eventHandlerClassLoader;
        aspectClassPath = new ArrayList<URL>();
        bridgeClassPath = new ArrayList<URL>();
    }

    public ClassLoader getEventHandlerClassLoader()
    {
        return eventHandlerClassLoader;
    }

    public List<URL> getAspectClassPath()
    {
        return Collections.unmodifiableList(aspectClassPath);
    }

    public List<URL> getBridgeClassPath()
    {
        return Collections.unmodifiableList(bridgeClassPath);
    }

    public void addAspectClassPath(URL p)
    {
        aspectClassPath.add(p);
    }

    public void addBridgeClassPath(URL p)
    {
        bridgeClassPath.add(p);
    }

    private final ClassLoader eventHandlerClassLoader;
    private final List<URL> aspectClassPath;
    private final List<URL> bridgeClassPath;
}
