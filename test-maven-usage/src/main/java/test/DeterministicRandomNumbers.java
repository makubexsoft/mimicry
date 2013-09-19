package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.gc.mimicry.ext.stdio.events.ConsoleOutputEvent;

public class DeterministicRandomNumbers
{
	public static void main( String[] args ) throws Exception
	{       
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
		
		EventBridge eventBridge = new EventBridge();
		eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(Event evt)
            {
            	if(evt instanceof ConsoleOutputEvent)
            	{
            		ConsoleOutputEvent e = (ConsoleOutputEvent)evt;
            		System.out.print("[console] " + new String(e.getData()));
            	}
            }
        });

		final ClassLoader loader = ApplicationClassLoader.create(config,  ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        Thread.currentThread().setContextClassLoader( loader );
        LocalApplication app = Applications.create(ctx, new RandomNumberTest());
        
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
			jobs.add( new TestJob() );
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
	@Override
	public void run()
	{
		Random rand = new Random(System.currentTimeMillis());
		System.out.println("Thread [" + Thread.currentThread().getName() + "] " + rand.nextInt( 10 ));
	}
}
