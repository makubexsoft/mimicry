package com.gc.mimicry.bridge;

public class BridgeNotFoundException extends RuntimeException
{

	private static final long	serialVersionUID	= -6483800791568700186L;

	public BridgeNotFoundException()
	{
		super();
	}

	public BridgeNotFoundException(String message, Throwable cause)
	{
		super( message, cause );
	}

	public BridgeNotFoundException(String message)
	{
		super( message );
	}

	public BridgeNotFoundException(Throwable cause)
	{
		super( cause );
	}
}
