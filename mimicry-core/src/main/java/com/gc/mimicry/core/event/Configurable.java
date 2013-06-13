package com.gc.mimicry.core.event;

public interface Configurable<T extends Configuration>
{

	public void configure( T configuration );
}
