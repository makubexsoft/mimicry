package tests.bundle2;

public class WaitAMonth
{
	public static void main( String[] args ) throws InterruptedException
	{
		final Object lock = new Object();
		synchronized ( lock )
		{
			lock.wait( 3600 * 24 * 30 );
		}
	}
}
