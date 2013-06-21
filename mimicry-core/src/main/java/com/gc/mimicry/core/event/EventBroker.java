package com.gc.mimicry.core.event;

public interface EventBroker
{
	public void fireEvent( Event event );

	public void addEventListener( EventListener l );

	public void removeEventListener( EventListener l );
}
