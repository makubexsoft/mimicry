package examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class UDPPingPongClient
{
	public static void main( String[] args ) throws NumberFormatException, UnknownHostException, IOException,
			InterruptedException
	{
		System.out.println( "PingPong-CLIENT starting..." );
		try
		{
			Random rand = new Random( System.currentTimeMillis() );

			DatagramSocket s = new DatagramSocket(  );

			for ( ;; )
			{
				int num = rand.nextInt( 10 );
				String msg = num + "\r\n";
				System.out.println( "Client: send(" + num + ") @ time(" + System.currentTimeMillis() + ") counter("
						+ Counter.incAndGet() + ")" );
				
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter( bout );
				writer.write( msg );
				writer.flush();
				
				byte[] data = bout.toByteArray();
				
				DatagramPacket packet = new DatagramPacket( data, data.length );
				packet.setAddress( InetAddress.getByName( args[0] ) );
				packet.setPort( Integer.parseInt( args[1] ) );
				s.send( packet );
				
				byte[] buffer = new byte[512];
				DatagramPacket recvPacket = new DatagramPacket( buffer, buffer.length );
				s.receive( recvPacket );
				BufferedReader reader = new BufferedReader( new InputStreamReader( new ByteArrayInputStream( buffer, 0, recvPacket.getLength() ) ) );
				
				String received = reader.readLine();
				System.out.println("Client: received(" + received + ")");
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
			System.out.println( "PingPongClient terminated." );
		}
	}
}
