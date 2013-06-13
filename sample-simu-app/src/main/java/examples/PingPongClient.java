package examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class PingPongClient
{
	public static void main( String[] args ) throws NumberFormatException, UnknownHostException, IOException,
			InterruptedException
	{
		try
		{
			System.out.println( "PingPongClient starting..." );
			System.out.println( "Thread: " + Thread.currentThread() );
			Random rand = new Random( System.currentTimeMillis() );

			Socket s = new Socket( args[0], Integer.parseInt( args[1] ) );
			System.out.println( "Client: socket = " + s );
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( s.getOutputStream() ) );
			BufferedReader reader = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
			for ( ;; )
			{
				int num = rand.nextInt( 10 );
				String msg = num + "\n";
				System.out.println( "Client: Ping: " + num + " @ " + System.currentTimeMillis() + " (counter="
						+ Counter.incAndGet() + ")" );
				writer.write( msg );
				writer.flush();

				String received = reader.readLine();
				if ( received == null )
				{
					System.out.println( "Terminating VM." );
					System.exit( 0 );
				}
				Thread.sleep( 1000 );
			}
		}
		finally
		{
			System.out.println( "PingPongClient end." );
		}
	}
}
