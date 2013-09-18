package test;

import java.net.Socket;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;
import org.aspectj.weaver.tools.WeavingAdaptor;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.local.Applications;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;

public class DeterministicSchedulingExample
{
	public static void main( String[] args ) throws Exception
	{
		System.setProperty("org.aspectj.tracing.debug", "true");
        System.setProperty("org.aspectj.tracing.enabled", "true");
        System.setProperty("org.aspectj.tracing.messages", "true");
        System.setProperty("aj.weaving.verbose", "true");
        System.setProperty(WeavingAdaptor.SHOW_WEAVE_INFO_PROPERTY, "true");
        System.setProperty(WeavingAdaptor.TRACE_MESSAGES_PROPERTY, "true");
        System.setProperty("org.aspectj.tracing.factory", "default");
        
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
		final ClassLoader loader = ApplicationClassLoader.create(config,  ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        Thread.currentThread().setContextClassLoader( loader );
        LocalApplication app = Applications.create(ctx, new EntryPoint()
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
						System.out.print("Thread-1: run ");
						for(int i = 0; i < 30; ++i)
						{
							synchronized(monitor)
							{
								System.out.print("Thread-1: " + i);
							}
						}
					}
				});
            	Thread t2 = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						System.out.print("Thread-2: run ");
						for(int i = 0; i < 30; ++i)
						{
							synchronized(monitor)
							{
								System.out.print("Thread-2: " + i);
							}
						}
					}
				});
            	Thread t3 = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						System.out.print("Thread-3: run");
						for(int i = 0; i < 30; ++i)
						{
							synchronized(monitor)
							{
								System.out.print("Thread-3: " + i);
							}
						}
					}
				});
            	
            	t1.start();
            	t2.start();
            	t3.start();
            	try
				{
					Thread.sleep( 1000 );
				}
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}
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
