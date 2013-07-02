package examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PingPongServer
{
	public static void main( String[] args ) throws NumberFormatException, UnknownHostException, IOException
	{
		try
		{
			System.out.println( "PingPong-SERVER starting..." );

			ServerSocket ss = new ServerSocket( Integer.parseInt( args[0] ) );

			Socket s = ss.accept();

			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( s.getOutputStream() ) );
			BufferedReader reader = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
			for ( ;; )
			{
				System.out.println("Server: waiting...");
				String received = reader.readLine();
				System.out.println("Server: received(" + received + ")");
				if ( received == null )
				{
					System.out.println( "Terminating VM." );
					System.exit( 0 );
				}

				int num = Integer.parseInt( received );
				String msg = (num + 1) + "\n";
				writer.write( msg );
				writer.flush();
				System.out.println( "Server: Pong: " + (num + 1) + " @ " + System.currentTimeMillis() + " (counter="
						+ Counter.incAndGet() + ")" );
			}
		}
		finally
		{
			System.out.println( "PingPongServer end." );
		}
	}
}
