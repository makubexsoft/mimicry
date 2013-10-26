package org.mimicry.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class ByteBuffer
{
	private final Object				lock	= new Object();
	private int							cursor;
	private byte[]						currentBuffer;
	private final LinkedList<byte[]>	buffers;

	public ByteBuffer()
	{
		currentBuffer = new byte[0];
		buffers = new LinkedList<byte[]>();
	}

	public int available()
	{
		return currentBuffer.length - cursor;
	}

	public InputStream createStream()
	{
		return new ByteBufferInputStream();
	}

	public int read()
	{
		if ( cursor < currentBuffer.length )
		{
			return currentBuffer[cursor++] & 0xFF;
		}
		synchronized ( lock )
		{
			while ( buffers.size() == 0 )
			{
				try
				{
					lock.wait();
				}
				catch ( InterruptedException e )
				{
				}
			}
			currentBuffer = buffers.poll();
		}
		cursor = 0;
		return currentBuffer[cursor++] & 0xFF;
	}

	public void write( byte[] buffer )
	{
		synchronized ( lock )
		{
			buffers.add( buffer );
			lock.notify();
		}
	}

	private class ByteBufferInputStream extends InputStream
	{
		@Override
		public int read() throws IOException
		{
			return ByteBuffer.this.read();
		}

		@Override
		public int read( byte[] b, int off, int len ) throws IOException
		{
			int av = available();
			if ( av == 0 )
			{
				av = 1;
			}
			if ( av > len )
			{
				av = len;
			}
			int i;
			for ( i = 0; i < av; i++ )
			{
				b[off + i] = (byte) (read() & 0xFF);
			}
			return i;
		}

		@Override
		public int read( byte[] b ) throws IOException
		{
			return read( b, 0, b.length );
		}

		@Override
		public int available() throws IOException
		{
			return ByteBuffer.this.available();
		}
	}
}
