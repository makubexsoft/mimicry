package com.gc.mimicry.bridge;

/**
 * The loop interceptor invokes on each loop iteration the configured
 * {@link LoopInterceptionStrategy}.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LoopInterceptor
{
	private static LoopInterceptionStrategy	strategy;

	public static void intercept()
	{
		if ( strategy != null )
		{
			strategy.intercept();
		}
	}

	public static void setStrategy( LoopInterceptionStrategy strategy )
	{
		LoopInterceptor.strategy = strategy;
	}
}
