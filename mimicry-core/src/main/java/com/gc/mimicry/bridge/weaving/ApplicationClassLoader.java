package com.gc.mimicry.bridge.weaving;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;

import com.gc.mimicry.util.ClassPathUtil;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

/**
 * {@link ClassLoader} implementation that uses the {@link LoopInterceptingByteCodeLoader} for loading and
 * pre-processing byte code of a simulated application. After the byte code has been pre-processed it's forwarded to
 * aspectJ in order to apply more aspects.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationClassLoader extends WeavingURLClassLoader
{
    private URLClassLoader sharedAppLoader;

    public ApplicationClassLoader(Collection<URL> classPath, Collection<URL> aspects, ClassLoader parent)
            throws MalformedURLException
    {
        super(classPath.toArray(new URL[0]), aspects.toArray(new URL[0]), parent);

        Preconditions.checkNotNull(aspects);

        Collection<URL> urls = Collections2.transform(ClassPathUtil.getSystemClassPath(), new Function<String, URL>()
        {
            @Override
            public URL apply(String f)
            {
                try
                {
                    return new File(f).toURI().toURL();
                }
                catch (MalformedURLException e)
                {
                    return null;
                }
            }
        });
        urls = new HashSet<URL>(urls);
        urls.addAll(classPath);

        sharedAppLoader = new WeavingURLClassLoader(urls.toArray(new URL[0]), aspects.toArray(new URL[0]), null)
        {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException
            {
                if (name.startsWith("com.gc.mimicry."))
                {
                    return ApplicationClassLoader.super.loadClass(name);
                }
                return super.findClass(name);
            }
        };
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null)
        {
            return clazz;
        }

        try
        {
            // First check our class path which contains the BRIDGE and ASPECTS
            return findClass(name);
        }
        catch (ClassNotFoundException e)
        {
            // System.out.println("[!! BRIDGE !!] " + name);
            // if it's no mimicry class we check a duplicated system class path
            // to allow reloading classes embedded in the application
            if (!name.startsWith("com.gc.mimicry."))
            {
                try
                {
                    return sharedAppLoader.loadClass(name);
                }
                catch (ClassNotFoundException ex)
                {
                    // now we need to load from parent
                }
            }
            // System.out.println("[SYSTEM] " + name);
            // It must be a class of the JRE or Mimicry itself
            return super.loadClass(name);
        }
    }
}
