package com.gc.mimicry.engine.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.threading.CheckpointBasedScheduler;
import com.gc.mimicry.engine.ApplicationContext;
import com.googlecode.transloader.DefaultTransloader;
import com.googlecode.transloader.ObjectWrapper;
import com.googlecode.transloader.Transloader;
import com.googlecode.transloader.clone.CloningStrategy;
import com.googlecode.transloader.clone.reflect.CloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.MinimalCloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.ObjenesisInstantiationStrategy;
import com.googlecode.transloader.clone.reflect.ReflectionCloningStrategy;

public class Applications
{
    public static LocalApplication create(final ApplicationContext ctx, EntryPoint runnable)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException
    {
        final EntryPoint entryPoint = bridge(ctx.getClassLoader(), runnable);

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
                thread.setContextClassLoader(ctx.getClassLoader());
                thread.start();
            }
        };
        return new LocalApplication(ctx, r, new CheckpointBasedScheduler(ctx.getClock()));
    }

    private static EntryPoint bridge(ClassLoader loader, EntryPoint runnable)
    {
        CloningDecisionStrategy cloneStrategy = new MinimalCloningDecisionStrategy();
        ObjenesisInstantiationStrategy createStrategy = new ObjenesisInstantiationStrategy();

        ReflectionCloningStrategy strategy;
        strategy = new ReflectionCloningStrategy(cloneStrategy, createStrategy, CloningStrategy.MINIMAL);

        Transloader transloader = new DefaultTransloader(strategy);

        ObjectWrapper someObjectWrapped = transloader.wrap(runnable);
        return (EntryPoint) someObjectWrapped.cloneWith(loader);
    }

    public static LocalApplication create(final ApplicationContext ctx, String mainClassName)
            throws NoSuchMethodException, SecurityException, ClassNotFoundException
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
                thread.setContextClassLoader(ctx.getClassLoader());
                thread.start();
            }
        };
        return new LocalApplication(ctx, r, new CheckpointBasedScheduler(ctx.getClock()));
    }
}
