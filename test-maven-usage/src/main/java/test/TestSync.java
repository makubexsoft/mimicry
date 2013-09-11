package test;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;

public class TestSync
{
    static int counter;

    public static void main(String[] args) throws Exception
    {
        ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();

        // per NODE
        //
        EventBridge eventBridge = new EventBridge();
        eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(Event evt)
            {
                System.out.println("[event] " + evt);
            }
        });

        // per APPLICATION
        //
        ClassLoader loader = ApplicationClassLoader.create(config, ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        Thread.currentThread().setContextClassLoader(loader);
        Application app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                final Object monitor = "My named Monitor";

                Thread t1 = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < 300; ++i)
                        {
                            synchronized (monitor)
                            {
                                System.out.print(Thread.currentThread().getName() + ": " + counter++);
                            }
                        }
                    }
                });
                Thread t2 = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int i = 0; i < 300; ++i)
                        {
                            synchronized (monitor)
                            {
                                System.out.print(Thread.currentThread().getName() + ": " + counter++);
                            }
                        }
                    }
                });

                t1.start();
                t2.start();
            }
        });

        // the SCRIPT
        //
        System.out.println("starting...");
        app.start();
        Thread.sleep(3000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
        
        
    }
}
