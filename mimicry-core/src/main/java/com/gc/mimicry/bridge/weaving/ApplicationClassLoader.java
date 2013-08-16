package com.gc.mimicry.bridge.weaving;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;

import com.gc.mimicry.engine.MimicryConfiguration;
import com.gc.mimicry.util.ClassPathUtil;

/**
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationClassLoader extends WeavingURLClassLoader
{
    public static ApplicationClassLoader create(MimicryConfiguration config) throws MalformedURLException
    {
        List<URL> aspects = new ArrayList<URL>();
        aspects.addAll(config.getAspectClassPath());

        Set<URL> classPath = new HashSet<URL>();
        classPath.addAll(config.getAspectClassPath());
        classPath.addAll(config.getBridgeClassPath());

        return new ApplicationClassLoader(classPath, aspects);
    }

    static class MyParentLoader extends WeavingURLClassLoader
    {
        private ApplicationClassLoader child;

        public MyParentLoader(Collection<URL> classpath, Collection<URL> aspects)
        {
            super(classpath.toArray(new URL[0]), aspects.toArray(new URL[0]), ApplicationClassLoader.class
                    .getClassLoader());

        }

        public void setChild(ApplicationClassLoader child)
        {
            this.child = child;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
            //
            // STAGE-0
            //
            Class<?> clazz0 = child.findLoadedClass(name);
            if (clazz0 != null)
            {
                return clazz0;
            }
            try
            {
                return child.findClass(name);
            }
            catch (ClassNotFoundException e)
            {
                // ignore - parent (System Class Loader) will pick it up
            }

            //
            // STAGE-1
            //
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null)
            {
                return clazz;
            }
            try
            {
                Class<?> c = findClass(name);
                System.out.println("[STAGE-1] " + name);
                return c;
            }
            catch (ClassNotFoundException e)
            {
                // ignore - parent (System Class Loader) will pick it up
            }

            //
            // STAGE-2
            //
            Class<?> c = getParent().loadClass(name);
            System.out.println("[STAGE-2] " + name);
            return c;
        }
    }

    ApplicationClassLoader(Collection<URL> classPath, Collection<URL> aspects)
    {
        super(ClassPathUtil.getSystemClassPath().toArray(new URL[0]), aspects.toArray(new URL[0]), new MyParentLoader(
                classPath, aspects));
        ((MyParentLoader) getParent()).setChild(this);
    }

    @Override
    protected Class findClass(String name) throws ClassNotFoundException
    {
        if (name.startsWith("com.gc.mimicry."))
        {
            throw new ClassNotFoundException(name);
        }
        Class c = super.findClass(name);
        System.out.println("[STAGE-0] " + name);
        return c;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        return getParent().loadClass(name);
    }
}
