package test;

import org.aspectj.weaver.tools.WeavingAdaptor;
import org.mimicry.ApplicationContext;
import org.mimicry.ClassPathConfiguration;
import org.mimicry.EventListener;
import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.Applications;
import org.mimicry.engine.EventBridge;
import org.mimicry.engine.LocalApplication;
import org.mimicry.timing.SystemClock;
import org.wso2.siddhi.core.SiddhiManager;


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
        
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromSystemClassLoader();
		CEPEngine eventEngine = new SiddhiCEPEngine();
		// per NODE
		//
		EventBridge eventBridge = new EventBridge(eventEngine);
		eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                System.out.println("[event] " + evt);
            }
        });

		// per APPLICATION
		//
		final ClassLoader loader = ApplicationClassLoader.create(config);
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
        }, eventEngine);
        
        // the SCRIPT
        //
        System.out.println("starting...");
        app.start();
        Thread.sleep(3000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
	}
}
