package com.gc.mimicry.engine;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.googlecode.transloader.DefaultTransloader;
import com.googlecode.transloader.ObjectWrapper;
import com.googlecode.transloader.Transloader;
import com.googlecode.transloader.clone.CloningStrategy;
import com.googlecode.transloader.clone.reflect.MinimalCloningDecisionStrategy;
import com.googlecode.transloader.clone.reflect.ObjenesisInstantiationStrategy;
import com.googlecode.transloader.clone.reflect.ReflectionCloningStrategy;

public class Application implements Closeable
{
    private final EntryPoint r;
    private final ApplicationBridge bridge;

    Application(ApplicationContext ctx, EntryPoint runnable) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException
    {
        Transloader transloader = new DefaultTransloader(new ReflectionCloningStrategy(
                new MinimalCloningDecisionStrategy(), new ObjenesisInstantiationStrategy(), CloningStrategy.MINIMAL));
        ObjectWrapper someObjectWrapped = transloader.wrap(runnable);

        final Object object = someObjectWrapped.cloneWith(ctx.getClassLoader());
        Class<?> wovenClass = ctx.getClassLoader().loadClass(EntryPoint.class.getName());
        final Method method = wovenClass.getMethod("main", String[].class);

        bridge = new ApplicationBridge(ctx.getClassLoader());
        bridge.setClock(ctx.getClock());
        bridge.setEventBridge(ctx.getEventBridge());
        bridge.setApplicationId(UUID.randomUUID());

        r = new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                try
                {
                    method.invoke(object, new Object[] { args });
                }
                catch (IllegalAccessException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    public void start(String... args)
    {
        r.main(args);
    }

    public void stop()
    {

    }

    @Override
    public void close()
    {

    }
}
