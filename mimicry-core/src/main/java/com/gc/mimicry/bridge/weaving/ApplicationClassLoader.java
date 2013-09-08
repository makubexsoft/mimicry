package com.gc.mimicry.bridge.weaving;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.util.ClassPathUtil;

/**
 * Specialized class loader for simulated applications. It applies the given aspects to the loaded classes. Internally
 * the class loader manages the classes at three stages which contain different classes.
 * <ul>
 * <li><b>Stage-2</b><br/>
 * Contains the JRE and Mimicry Core classes. There is only a single stage-2. Classes loaded in this stage are not
 * considered in the weaving process.</li>
 * <li><b>Stage-1</b><br/>
 * Contains the Mimicry Bridge and Aspects. This stage gets instantiated per simulated application. Classes of this
 * stage are woven using the given aspects.</li>
 * <li><b>Stage-0</b><br/>
 * Contains the classes of the simulated application. This stage gets instantiated for each stage-1. Classes of this
 * stage are woven using the given aspects.</li>
 * </ul>
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

    public static ApplicationClassLoader create(ClassPathConfiguration config, Collection<URL> applicationClassPath)
            throws MalformedURLException
    {
        Set<URL> classPath = new HashSet<URL>(applicationClassPath);
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

    private static byte[] removeFinalizer(byte[] bytes) throws IOException, RuntimeException, CannotCompileException
    {
        CtClass clazz = new ClassPool().makeClass(new ByteArrayInputStream(bytes));
        if (clazz.isInterface())
        {
            return bytes;
        }

        try
        {
            CtMethod method = clazz.getMethod("finalize", "()V");
            if (null != method && !method.isEmpty())
            {
                clazz.removeMethod(method);
                bytes = clazz.toBytecode();
            }
        }
        catch (NotFoundException ignore)
        {
        }

        return bytes;
    }

    private static class MyParentLoader extends WeavingURLClassLoader
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
