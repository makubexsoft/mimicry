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
			System.out.println( "PingPongServer starting..." );

			ServerSocket ss = new ServerSocket( Integer.parseInt( args[0] ) );
			System.out.println( "Server: socket = " + ss );
			Socket s = ss.accept();
			System.out.println( "Server: accepted " + s.getRemoteSocketAddress() );
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( s.getOutputStream() ) );
			BufferedReader reader = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
			for ( ;; )
			{
				String received = reader.readLine();
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
