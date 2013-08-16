package com.gc.mimicry.engine;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.threading.ThreadManager;
import com.gc.mimicry.util.concurrent.Future;
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
    private final ThreadManager threadManager;

    Application(ApplicationContext ctx, EntryPoint runnable) throws ClassNotFoundException, NoSuchMethodException,
            SecurityException
    {
        Transloader transloader = new DefaultTransloader(new ReflectionCloningStrategy(
                new MinimalCloningDecisionStrategy(), new ObjenesisInstantiationStrategy(), CloningStrategy.MINIMAL));
        ObjectWrapper someObjectWrapped = transloader.wrap(runnable);

        final Object object = someObjectWrapped.cloneWith(ctx.getClassLoader());

        // System.out.println(object.getClass().getInterfaces()[0].getClassLoader());
        // System.out.println(EntryPoint.class.getClassLoader());

        final EntryPoint e = (EntryPoint) object;

        threadManager = new ThreadManager(UUID.randomUUID());
        // Class<?> wovenClass = ctx.getClassLoader().loadClass(EntryPoint.class.getName());
        // final Method method = EntryPoint.class.getMethod("main", String[].class);

        Class<?> threadClass = ctx.getClassLoader().loadClass("com.gc.mimicry.bridge.threading.ManagedThread");
        final Constructor<?> constructor = threadClass.getConstructor(Runnable.class);

        bridge = new ApplicationBridge(ctx.getClassLoader());
        bridge.setClock(ctx.getClock());
        bridge.setEventBridge(ctx.getEventBridge());
        bridge.setThreadManager(threadManager);

        r = new EntryPoint()
        {
            @Override
            public void main(final String[] args)
            {
                try
                {
                    Thread thread = (Thread) constructor.newInstance(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            e.main(args);
                        }
                    });
                    thread.start();
                }
                catch (InstantiationException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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

    public Future<?> stop()
    {
        return threadManager.shutdownAllThreads();
    }

    @Override
    public void close()
    {

    }
}
