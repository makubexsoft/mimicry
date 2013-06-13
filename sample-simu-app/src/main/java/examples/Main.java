package examples;

import java.util.Random;

public class Main
{

	public static void main( String[] args )
	{
		Random rand = new Random( System.nanoTime() );
		int id = rand.nextInt();

		try
		{
			System.out.println( "[" + id + "] Application starting..." );

			for ( ;; )
			{
				System.out.println( "[" + id + "] Running..." );
				try
				{
					Thread.sleep( 1000 );
				}
				catch ( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		}
		finally
		{
			System.out.println( "[" + id + "] Application end." );
		}
	}
}
