package com.gc.mimicry.bridge;

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
