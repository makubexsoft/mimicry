package com.gc.mimicry.bridge.weaving;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

/**
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LoopInterceptingByteCodeLoader
{
    public LoopInterceptingByteCodeLoader(String[] classUrls)
    {
        List<URL> urls = constructClassPath();
        classLoader = new URLClassLoader(urls.toArray(new URL[0]));
        try
        {
            Class<?> loaderClass = classLoader.loadClass(LOADER_CLASS_NAME);
            Constructor<?> constructor = loaderClass.getConstructor(String[].class);
            loaderInstance = constructor.newInstance((Object) classUrls);
            loaderMethod = loaderClass.getDeclaredMethod(LOADER_METHOD_NAME, String.class);
        }
        catch (Exception e)
        {
            logger.error(FAILED_TO_LOAD_MSG, e);
            throw new RuntimeException(FAILED_TO_LOAD_MSG, e);
        }
    }

    public byte[] loadTransformedByteCode(String className)
    {
        try
        {
            synchronized (loaderMethod)
            {
                return (byte[]) loaderMethod.invoke(loaderInstance, className);
            }
        }
        catch (Exception e)
        {
            logger.error(FAILED_TO_INVOKE_MSG, e);
        }
        return null;
    }

    private List<URL> constructClassPath()
    {
        List<URL> urls = new ArrayList<URL>();
        String classPath = System.getProperty("java.class.path");
        for (String path : Splitter.on(File.pathSeparator).split(classPath))
        {
            try
            {
                urls.add(new URL("file://" + path));
            }
            catch (MalformedURLException e)
            {
                // should not happen
                logger.error("Something went terribly wrong. Take a look at that classpath: " + classPath, e);
            }
        }
        return urls;
    }

    private final ClassLoader classLoader;
    private final Object loaderInstance;
    private final Method loaderMethod;
    private static final Logger logger;
    private static final String FAILED_TO_LOAD_MSG;
    private static final String FAILED_TO_INVOKE_MSG;
    private static final String LOADER_CLASS_NAME;
    private static final String LOADER_METHOD_NAME;
    static
    {
        logger = LoggerFactory.getLogger(LoopInterceptingByteCodeLoader.class);
        FAILED_TO_LOAD_MSG = "Failed to load SootByteCodeLoader";
        FAILED_TO_INVOKE_MSG = "Failed to invoke SootByteCodeLoader";
        LOADER_CLASS_NAME = "com.gc.mimicry.bridge.weaving.SootByteCodeLoader";
        LOADER_METHOD_NAME = "loadBytes";
    }
}
