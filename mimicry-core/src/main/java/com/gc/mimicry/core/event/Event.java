package com.gc.mimicry.core.event;

import java.io.Serializable;

public interface Event extends Serializable
{
	public abstract boolean isResponseTo( Event request );

}
