package com.gc.mimicry.bridge;

import com.gc.mimicry.bridge.weaving.LoopInterceptionStrategy;

public class CountingInterceptionStrategy implements LoopInterceptionStrategy
{

	private int	counter;

	public void intercept()
	{
		counter++;
	}

	public int getCounter()
	{
		return counter;
	}
}
