package test;

public class ExampleSync
{
	static final Object lock = new Object();
	static Thread t1;
	
	public static void main( String[] args )
	{
		t1 = new Thread(){
			@Override
			public void run()
			{
				System.out.println("Start Thread 1");
				foo();
			}
		};
		t1.start();
	}

	public static void foo()
	{
		synchronized ( lock )
		{
			try
			{
				Thread t2 = new Thread(){
					@Override
					public void run()
					{
						synchronized ( lock )
						{
							System.out.println("sending interrupt...");							
							t1.interrupt();
							System.out.println("now blocking monitor for 5 seconds...");
							try
							{
								Thread.sleep( 5000 );
							}
							catch ( InterruptedException e )
							{
								e.printStackTrace();
							}
							System.out.println("now releasing monitor...");
						}
						System.out.println("monitor released.");
					}
				};
				t2.start();
				
				System.out.println("Waiting...");
				lock.wait();
			}
			catch ( InterruptedException e )
			{
				System.err.println("Caught exception.");
			}
		}
	}
	
}
