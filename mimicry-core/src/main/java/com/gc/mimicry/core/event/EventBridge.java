package com.gc.mimicry.core.event;

import java.util.WeakHashMap;

/**
 * The event bridge is located between the {@link EventStack} and the woven
 * aspects. Once the application becomes deactivated the bridge no longer
 * forwards events in any direction. Furthermore the bridge consumes upstream
 * events that are responses to pending requests. Other events are passed to the
 * registered event listeners which are typically the managed instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class EventBridge
{

	private final WeakHashMap<EventListener, Boolean>	listener;

	public EventBridge()
	{
		listener = new WeakHashMap<EventListener, Boolean>();
	}

	/**
	 * Emits the given event and waits an infinite time for a corresponding
	 * response event. This method is invoked by the woven user code.
	 * 
	 * @param evt
	 * @return
	 */
	public Event emitAndWait( Event evt )
	{
		eventOccured( evt );

		return null;
	}

	/**
	 * Emits the given event and waits the given time for a corresponding
	 * response event. This method is invoked by the woven user code.
	 * 
	 * @param evt
	 * @param timeoutMillis
	 * @return
	 */
	public Event emitAndWait( Event evt, long timeoutMillis )
	{
		eventOccured( evt );

		return null;
	}

	/**
	 * Emits the given event and directly returns to the caller. This method is
	 * invoked by the woven user code.
	 * 
	 * @param evt
	 */
	public void emit( Event evt )
	{
		eventOccured( evt );

	}

	private void eventOccured( Event evt )
	{

	}

	/**
	 * Adds a weak reference to the given listener to this bridge. The listener
	 * is invoked for each event that has passed the event bridge and was not a
	 * response to any pending request. Multiple invocations with the same
	 * argument will be ignored.
	 * 
	 * @param l
	 */
	public void addEventListener( EventListener l )
	{
		listener.put( l, true );
	}

	/**
	 * Removes the given listener from the list. Multiple invocations with the
	 * same argument will be ignored.
	 * 
	 * @param l
	 */
	public void removeEventListener( EventListener l )
	{
		listener.remove( l );
	}

}
