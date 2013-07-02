package com.gc.mimicry.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Utility class for creating proxies of interfaces via the JDK's {@link Proxy} support or for classes using CgLib.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ProxyFactory
{
    private ProxyFactory()
    {
    }

    public static <T> T createProxy(ClassLoader loader, Class<T> superClass, InvocationHandler h)
    {
        if (superClass.isInterface())
        {
            return superClass.cast(createJdkProxy(loader, new Class[] { superClass }, h));
        }
        else
        {
            return superClass.cast(createCgLibProxy(superClass, null, h));
        }
    }

    private static Object createJdkProxy(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
    {
        return Proxy.newProxyInstance(loader, interfaces, h);
    }

    private static Object createCgLibProxy(Class<?> superClass, Class<?>[] interfaces, InvocationHandler h)
    {
        return Enhancer.create(superClass, interfaces, new HandlerAdapter(h));
    }

    private static class HandlerAdapter implements MethodInterceptor
    {
        private final InvocationHandler handler;

        public HandlerAdapter(InvocationHandler handler)
        {
            this.handler = handler;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
        {
            return handler.invoke(obj, method, args);
        }
    }
}
