package com.gc.mimicry.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassLoadingContext
{
    public ClassLoadingContext(ClassLoader coreClassLoader)
    {
        this.coreClassLoader = coreClassLoader;
        aspectClassPath = new ArrayList<URL>();
        bridgeClassPath = new ArrayList<URL>();
    }

    public ClassLoader getCoreClassLoader()
    {
        return coreClassLoader;
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

    private final ClassLoader coreClassLoader;
    private final List<URL> aspectClassPath;
    private final List<URL> bridgeClassPath;
}
