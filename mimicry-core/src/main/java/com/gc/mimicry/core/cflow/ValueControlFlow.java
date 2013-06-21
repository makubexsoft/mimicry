package com.gc.mimicry.core.cflow;

import com.gc.mimicry.util.concurrent.DefaultValueFuture;
import com.gc.mimicry.util.concurrent.ValueFuture;

public class ValueControlFlow<T> extends ControlFlow
{
	private ValueFuture<T>	future;

	public ValueControlFlow()
	{
		future = new DefaultValueFuture<T>();
	}

	public ValueFuture<T> getFuture()
	{
		return future;
	}
}
