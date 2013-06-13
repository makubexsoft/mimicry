package com.gc.mimicry.bridge.threading;

public interface ThreadShutdownListener
{
	public void threadShouldTerminate( IManagedThread thread );
}
