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
    public static ClassPathConfiguration deriveFromSystemClassLoader() throws MalformedURLException
    {
        ClassLoader loader = ClassLoader.getSystemClassLoader();

        File bridgeJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/SimulatorBridge.class"));
        File aspectJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/aspects/ConsoleAspect.class"));

        ClassPathConfiguration ctx = new ClassPathConfiguration(loader);
        ctx.addAspectClassPath(aspectJar.toURI().toURL());
        ctx.addToStage1ClassPath(bridgeJar.toURI().toURL());
        for (URL url : ClassPathUtil.getSystemClassPath())
        {
            ctx.addToStage2ClassPath(url);
        }

        return ctx;
    }

    public static ClassPathConfiguration createEmpty() throws MalformedURLException
    {
        return new ClassPathConfiguration(ClassLoader.getSystemClassLoader());
    }

    ClassPathConfiguration(ClassLoader stage0ClassLoader)
    {
        this.stage0ClassLoader = stage0ClassLoader;
        aspectClassPath = new ArrayList<URL>();
        stage1ClassPath = new ArrayList<URL>();
        stage2ClassPath = new ArrayList<URL>();
    }

    public ClassLoader getStage0ClassLoader()
    {
        return stage0ClassLoader;
    }

    public List<URL> getAspectClassPath()
    {
        return Collections.unmodifiableList(aspectClassPath);
    }

    public List<URL> getStage1ClassPath()
    {
        return Collections.unmodifiableList(stage1ClassPath);
    }

    public List<URL> getStage2ClassPath()
    {
        return Collections.unmodifiableList(stage2ClassPath);
    }

    public void addAspectClassPath(URL p)
    {
        aspectClassPath.add(p);
    }

    public void addToStage1ClassPath(URL p)
    {
        stage1ClassPath.add(p);
    }

    public void addToStage2ClassPath(URL p)
    {
        stage2ClassPath.add(p);
    }

    private final ClassLoader stage0ClassLoader;
    private final List<URL> aspectClassPath;
    private final List<URL> stage1ClassPath;
    private final List<URL> stage2ClassPath;
}
