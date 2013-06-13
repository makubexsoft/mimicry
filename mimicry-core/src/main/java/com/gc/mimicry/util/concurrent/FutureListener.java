package com.gc.mimicry.util.concurrent;

public interface FutureListener<T>
{

	public void operationComplete( T future );
}
