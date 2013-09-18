package com.gc.mimicry.engine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gc.mimicry.util.ClassPathUtil;

public class ClassPathConfiguration
{
    public static ClassPathConfiguration deriveFromClassPath() throws MalformedURLException
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        File bridgeJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/SimulatorBridge.class"));
        File aspectJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/aspects/ConsoleAspect.class"));
        File coreJar = new File(ClassPathUtil.getResourceLocation(loader, "com/gc/mimicry/engine/Application.class"));

        ClassPathConfiguration ctx = new ClassPathConfiguration(loader);
        ctx.addAspectClassPath(aspectJar.toURI().toURL());
        ctx.addBridgeClassPath(bridgeJar.toURI().toURL());
        ctx.addCoreClassPath(coreJar.toURI().toURL());

        return ctx;
    }

    public static ClassPathConfiguration createEmpty() throws MalformedURLException
    {
        return new ClassPathConfiguration(ClassLoader.getSystemClassLoader());
    }

    ClassPathConfiguration(ClassLoader loader)
    {
        this.eventHandlerClassLoader = loader;
        aspectClassPath = new ArrayList<URL>();
        bridgeClassPath = new ArrayList<URL>();
        appClassPath = new ArrayList<URL>();
        coreClassPath = new ArrayList<URL>();
    }

    public ClassLoader getEventHandlerClassLoader()
    {
        return eventHandlerClassLoader;
    }

    public List<URL> getAspectClassPath()
    {
        return Collections.unmodifiableList(aspectClassPath);
    }

    public List<URL> getCoreClassPath()
    {
        return Collections.unmodifiableList(coreClassPath);
    }

    public List<URL> getBridgeClassPath()
    {
        return Collections.unmodifiableList(bridgeClassPath);
    }

    public List<URL> getAppClassPath()
    {
        return Collections.unmodifiableList(appClassPath);
    }

    public void addAspectClassPath(URL p)
    {
        aspectClassPath.add(p);
    }

    public void addCoreClassPath(URL p)
    {
        coreClassPath.add(p);
    }

    public void addBridgeClassPath(URL p)
    {
        bridgeClassPath.add(p);
    }

    public void addAppClassPath(URL p)
    {
        appClassPath.add(p);
    }

    private final ClassLoader eventHandlerClassLoader;
    private final List<URL> aspectClassPath;
    private final List<URL> bridgeClassPath;
    private final List<URL> coreClassPath;
    private final List<URL> appClassPath;
}
