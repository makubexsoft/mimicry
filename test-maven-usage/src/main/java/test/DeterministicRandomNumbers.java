package test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.ApplicationContext;
import org.mimicry.engine.ClassPathConfiguration;
import org.mimicry.engine.EventListener;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.local.Applications;
import org.mimicry.engine.local.LocalApplication;
import org.mimicry.engine.stack.EventBridge;
import org.mimicry.engine.timing.SystemClock;
import org.mimicry.ext.stdio.events.ConsoleOutputEvent;


public class DeterministicRandomNumbers
{
	public static void main( String[] args ) throws Exception
	{       
		CEPEngine eventEngine = new SiddhiCEPEngine();
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromSystemClassLoader();
		ClassLoader loader = ApplicationClassLoader.create(config);
		EventBridge eventBridge = new EventBridge(eventEngine);
		
		eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(ApplicationEvent evt)
            {
            	if(evt instanceof ConsoleOutputEvent)
            	{
            		ConsoleOutputEvent e = (ConsoleOutputEvent)evt;
            		System.out.print("[console] " + new String(e.getData()));
            	}
            }
        });

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        Thread.currentThread().setContextClassLoader( loader );
        LocalApplication app = Applications.create(ctx, new RandomNumberTest(), eventEngine);
        
        app.start();
        app.getTerminationFuture().awaitUninterruptibly(5000);
	}
}

class RandomNumberTest implements EntryPoint 
{
	private List<Runnable> jobs = new ArrayList<Runnable>();
	
	@Override
	public void main( String[] args ) throws Throwable
	{
		for(int i =0; i < 100; ++i)
		{
			jobs.add( new TestJob(i) );
		}
		
		Runnable scheduleTask = new Runnable()
		{
			
			@Override
			public void run()
			{
				for(;;)
				{
					Runnable nextJob = null;
					synchronized ( jobs )
					{
						if(jobs.size() == 0)
						{
							return;
						}
						nextJob = jobs.remove( 0 );
					}
					if(nextJob != null)
					{
						nextJob.run();
					}
				}
			}
		};
		
		Thread t1 = new Thread(scheduleTask, "1");
		Thread t2 = new Thread(scheduleTask, "2");
		Thread t3 = new Thread(scheduleTask, "3");
		
		t1.start();
		t2.start();
		t3.start();
		
		t1.join();
		t2.join();
		t3.join();
	} 
}

class TestJob implements Runnable
{
	private int id;
	
	public TestJob(int id)
	{
		this.id = id;
	}
	
	@Override
	public void run()
	{
		Random rand = new SecureRandom(); // System.currentTimeMillis()
		System.out.println("Thread [" + Thread.currentThread().getName() + "] Job ["+id+"] " + rand.nextInt( 10 ));
	}
}
