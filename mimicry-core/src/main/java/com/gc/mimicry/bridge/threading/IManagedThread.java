package com.gc.mimicry.bridge.threading;

import com.gc.mimicry.core.StructuredId;

public interface IManagedThread
{
	public StructuredId getStructuredId();

	public boolean isShuttingDown();

	public void shutdownGracefully();

	public void addThreadShutdownListener( ThreadShutdownListener l );

	public void removeThreadShutdownListener( ThreadShutdownListener l );
}
