package com.gc.mimicry.util.concurrent;

import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultFuture extends AbstractFuture<DefaultFuture> implements Future<DefaultFuture>
{

	private final CopyOnWriteArrayList<FutureListener<? super DefaultFuture>>	listener;

	public DefaultFuture()
	{
		listener = new CopyOnWriteArrayList<FutureListener<? super DefaultFuture>>();
	}

	public void addFutureListener( final FutureListener<? super DefaultFuture> l )
	{
		doSynchronized( new Runnable()
		{

			public void run()
			{
				listener.add( l );
				if ( isDone() )
				{
					l.operationComplete( DefaultFuture.this );
				}
			}
		} );
	}

	public void removeFutureListener( FutureListener<? super DefaultFuture> l )
	{
		listener.remove( l );
	}

	@Override
	protected void notifyListener()
	{
		for ( FutureListener<? super DefaultFuture> l : listener )
		{
			l.operationComplete( this );
		}
	}

	@Override
	protected boolean performCancellation()
	{
		return false;
	}
}
