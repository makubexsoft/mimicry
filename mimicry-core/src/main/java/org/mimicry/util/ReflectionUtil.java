package org.mimicry.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtil
{
    private ReflectionUtil(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    public static ReflectionUtil createFor(ClassLoader classLoader, String className)
    {
        try
        {
            Class<?> clazz = classLoader.loadClass(className);
            return new ReflectionUtil(clazz);
        }
        catch (ClassNotFoundException e)
        {
            logger.debug("Can't create reflection util for class: " + className, e);
            return null;
        }
    }

    public static ReflectionUtil createFor(String className)
    {
        try
        {
            Class<?> clazz = Class.forName(className);
            return new ReflectionUtil(clazz);
        }
        catch (ClassNotFoundException e)
        {
            logger.debug("Can't create reflection util for class: " + className, e);
            return null;
        }
    }

    public boolean selectMethod(String name, Class<?>... paramTypes)
    {
        try
        {
            method = clazz.getMethod(name, paramTypes);
            return true;
        }
        catch (NoSuchMethodException e)
        {
            logger.debug("Method name=" + name + " with parameters " + paramTypes + " doesn't exist.", e);
            return false;
        }
        catch (SecurityException e)
        {
            logger.debug("Method name=" + name + " with parameters " + Arrays.asList(paramTypes)
                    + " can't be selected due to security constraints.", e);
            return false;
        }
    }

    public Object invokeStatic(Object... params)
    {
        try
        {
            return method.invoke(null, params);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
    }

    public Object invoke(Object instance, Object... params)
    {
        try
        {
            return method.invoke(instance, params);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException("Failed to invoke selected method: " + method, e);
        }
    }

    public Object newInstance()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            logger.debug("Failed to instantiate class " + clazz, e);
            return null;
        }
        catch (IllegalAccessException e)
        {
            logger.debug("Failed to instantiate class " + clazz, e);
            return null;
        }
    }

    public Class<?> getActualClass()
    {
        return clazz;
    }

    public Method getSelectedMethod()
    {
        return method;
    }

    private Method method;
    private final Class<?> clazz;
    private static Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ReflectionUtil.class);
    }
}
