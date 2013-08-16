package com.gc.mimicry.bridge.weaving;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.util.ClassPathUtil;

/**
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationClassLoader extends WeavingURLClassLoader
{
    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ApplicationClassLoader.class);
    }

    public static ApplicationClassLoader create(ClassPathConfiguration config) throws MalformedURLException
    {
        Set<URL> classPath = new HashSet<URL>();
        classPath.addAll(config.getAspectClassPath());
        classPath.addAll(config.getBridgeClassPath());

        return new ApplicationClassLoader(classPath, config.getAspectClassPath());
    }

    ApplicationClassLoader(Collection<URL> classPath, Collection<URL> aspects)
    {
        super(ClassPathUtil.getSystemClassPath().toArray(new URL[0]), aspects.toArray(new URL[0]), new MyParentLoader(
                classPath, aspects));
        ((MyParentLoader) getParent()).setChild(this);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        if (name.startsWith("com.gc.mimicry."))
        {
            throw new ClassNotFoundException(name);
        }
        Class<?> c = super.findClass(name);
        if (logger.isDebugEnabled())
        {
            logger.debug("[STAGE-0] " + name);
        }
        return c;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        return getParent().loadClass(name);
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
                if (logger.isDebugEnabled())
                {
                    logger.debug("[STAGE-1] " + name);
                }
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
            if (logger.isDebugEnabled())
            {
                logger.debug("[STAGE-2] " + name);
            }
            return c;
        }
    }
}
