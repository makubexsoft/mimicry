package com.gc.mimicry.engine.local;

import java.lang.reflect.Constructor;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.threading.CheckpointBasedScheduler;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.googlecode.transloader.DefaultTransloader;
import com.googlecode.transloader.ObjectWrapper;
import com.googlecode.transloader.Transloader;
import com.googlecode.transloader.clone.CloningStrategy;
import com.googlecode.transloader.clone.reflect.CloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.MinimalCloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.ObjenesisInstantiationStrategy;
import com.googlecode.transloader.clone.reflect.ReflectionCloningStrategy;

/**
 * Utility class for creating {@link Application} instances from an {@link EntryPoint} that was already loaded and
 * instantiated by a {@link ClassLoader} within the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Applications
{
    public static LocalApplication create(final ApplicationContext ctx, EntryPoint runnable, CEPEngine engine)
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
        return new LocalApplication(ctx, r, new CheckpointBasedScheduler(ctx.getTimeline()), engine);
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
}