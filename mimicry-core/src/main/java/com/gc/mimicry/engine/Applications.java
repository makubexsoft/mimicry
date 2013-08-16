package com.gc.mimicry.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.googlecode.transloader.DefaultTransloader;
import com.googlecode.transloader.ObjectWrapper;
import com.googlecode.transloader.Transloader;
import com.googlecode.transloader.clone.CloningStrategy;
import com.googlecode.transloader.clone.reflect.MinimalCloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.ObjenesisInstantiationStrategy;
import com.googlecode.transloader.clone.reflect.ReflectionCloningStrategy;

public class Applications
{

    public static Application create(ApplicationContext ctx, EntryPoint runnable) throws ClassNotFoundException,
            NoSuchMethodException, SecurityException
    {
        MinimalCloningDecisionStrategy cloneStrategy = new MinimalCloningDecisionStrategy();
        ObjenesisInstantiationStrategy createStrategy = new ObjenesisInstantiationStrategy();
        ReflectionCloningStrategy strategy;
        strategy = new ReflectionCloningStrategy(cloneStrategy, createStrategy, CloningStrategy.MINIMAL);
        Transloader transloader = new DefaultTransloader(strategy);
        ObjectWrapper someObjectWrapped = transloader.wrap(runnable);

        final EntryPoint entryPoint = (EntryPoint) someObjectWrapped.cloneWith(ctx.getClassLoader());

        Class<?> threadClass = ctx.getClassLoader().loadClass("com.gc.mimicry.bridge.threading.ManagedThread");
        final Constructor<?> constructor = threadClass.getConstructor(Runnable.class);
        EntryPoint r = new EntryPoint()
        {
            @Override
            public void main(final String[] args) throws Throwable
            {
                Thread thread = (Thread) constructor.newInstance(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            entryPoint.main(args);
                        }
                        catch (Throwable e)
                        {
                            throw new RuntimeException("Thread terminated due to uncaught exception.", e);
                        }
                    }
                });
                thread.start();
            }
        };
        return new Application(ctx, r);
    }

    public static Application create(ApplicationContext ctx, String mainClassName) throws NoSuchMethodException,
            SecurityException, ClassNotFoundException
    {
        Class<?> mainClass = ctx.getClassLoader().loadClass(mainClassName);
        final Method mainMethod = mainClass.getMethod("main", String[].class);

        Class<?> threadClass = ctx.getClassLoader().loadClass("com.gc.mimicry.bridge.threading.ManagedThread");
        final Constructor<?> constructor = threadClass.getConstructor(Runnable.class);

        EntryPoint r = new EntryPoint()
        {
            @Override
            public void main(final String[] args) throws Throwable
            {
                Thread thread = (Thread) constructor.newInstance(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            mainMethod.invoke(null, new Object[] { args });
                        }
                        catch (Throwable e)
                        {
                            throw new RuntimeException("Thread terminated due to uncaught exception.", e);
                        }
                    }
                });
                thread.start();
            }
        };
        return new Application(ctx, r);
    }
}
