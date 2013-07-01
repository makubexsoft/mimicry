package com.gc.mimicry.shared.events;

import java.io.Serializable;
import java.util.UUID;

/**
 * Basic interface for all events of the system.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface Event extends Serializable
{
	/**
	 * Returns the id of the associated control flow or null if no control flow
	 * is associated.
	 * 
	 * @return
	 */
	public UUID getAssociatedControlFlow();

	/**
	 * Returns the id of the application which caused this event or null if this
	 * event was not caused by an application.
	 * 
	 * @return
	 */
	public UUID getSourceApplication();

	/**
	 * Returns the id of the application this event is destined for or null if
	 * not directly destined for a application.
	 * 
	 * @return
	 */
	public UUID getTargetApplication();
}
