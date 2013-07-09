package examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class UDPPingPongServer
{
	public static void main( String[] args ) throws NumberFormatException, UnknownHostException, IOException
	{
		try
		{
			System.out.println( "PingPong-SERVER starting..." );


			DatagramSocket s = new DatagramSocket( Integer.parseInt( args[0] ) );

			for ( ;; )
			{
				System.out.println("Server: waiting...");
				
				byte[] buffer = new byte[512];
				DatagramPacket recvPacket = new DatagramPacket( buffer, buffer.length );
				s.receive( recvPacket );
				
				BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( buffer, 0, recvPacket.getLength() ) ) );
				String received = reader.readLine();
				System.out.println("Server: received(" + received + ")");
				if ( received == null )
				{
					System.out.println( "Terminating VM." );
					System.exit( 0 );
				}

				int num = Integer.parseInt( received );
				String msg = (num + 1) + "\n";
				
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter( bout );
				writer.write( msg );
				writer.flush();
				
				byte[] data = bout.toByteArray();
				
				DatagramPacket packet = new DatagramPacket( data, data.length );
				packet.setAddress( recvPacket.getAddress() );
				packet.setPort( recvPacket.getPort() );
				s.send( packet );

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
