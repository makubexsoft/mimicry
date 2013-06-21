package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.event.EventStack;
import com.gc.mimicry.core.messaging.MessagingSystem;
import com.google.common.base.Preconditions;

/**
 * A node represents a logical machine on which simulated applications can be
 * run. An instance of a node only exists within a certain simulation session.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class Node extends BaseResourceManager
{
	private final String				name;
	private  EventStack			eventStack;
	private final ApplicationManager	appMgr;

	Node(ClassLoadingContext context, String name, MessagingSystem messaging)
	{
		Preconditions.checkNotNull( name );
		Preconditions.checkNotNull( messaging );

		this.name = name;
		appMgr = new ApplicationManager( context, this );
		attachResource( appMgr );

		//TODO: eventStack = new EventStack( this, messaging );
	}

	public EventStack getEventStack()
	{
		return eventStack;
	}

	public String getName()
	{
		return name;
	}

	public ApplicationManager getApplicationManager()
	{
		return appMgr;
	}
}
