package com.gc.mimicry.core.timing;

import java.util.concurrent.TimeUnit;

public interface Scheduler
{

	public void schedule( Runnable job, long delay, TimeUnit unit );
}
