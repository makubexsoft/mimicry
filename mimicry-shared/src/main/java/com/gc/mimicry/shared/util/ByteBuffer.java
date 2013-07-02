package com.gc.mimicry.shared.util;

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
}
