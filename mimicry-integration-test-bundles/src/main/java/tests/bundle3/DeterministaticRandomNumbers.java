package tests.bundle3;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DeterministaticRandomNumbers
{
	private static List<Runnable> jobs = new ArrayList<Runnable>();
	
	public static void main( String[] args ) throws Throwable
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
		Random rand = new SecureRandom();
		System.out.print("Thread [" + Thread.currentThread().getName() + "] Job ["+id+"] " + rand.nextInt( 10 ));
	}
}