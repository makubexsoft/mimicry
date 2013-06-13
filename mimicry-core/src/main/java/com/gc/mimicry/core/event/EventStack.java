package com.gc.mimicry.core.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.runtime.Node;

public class EventStack
{

	private final Node					node;
	private final List<EventHandler>	handlerList;

	public EventStack(Node node, MessagingSystem messaging)
	{
		this.node = node;
		handlerList = new CopyOnWriteArrayList<EventHandler>();
	}

	public Node getNode()
	{
		return node;
	}

	void sendDownstream( int index, Event evt )
	{
		if ( handlerList.size() > index + 1 )
		{
			int nextIndex = index + 1;
			EventHandlerContext ctx = new EventHandlerContext( this, nextIndex );
			EventHandler handler = handlerList.get( nextIndex );
			handler.handleDownstream( ctx, evt );
		}
		else
		{
			// reached bottom

		}
	}

	void sendUpstream( int index, Event evt )
	{
		if ( index > 0 )
		{
			int nextIndex = index - 1;
			EventHandlerContext ctx = new EventHandlerContext( this, nextIndex );
			EventHandler handler = handlerList.get( nextIndex );
			handler.handleUpstream( ctx, evt );
		}
		else
		{
			// reached top

		}
	}

	public void addHandler( EventHandler handler )
	{
		handlerList.add( handler );
	}

	public void insertHandler( int index, EventHandler handler )
	{
		handlerList.add( index, handler );
	}

	public void removeHandler( EventHandler handler )
	{
		handlerList.remove( handler );
	}

	public EventHandler removeHandler( Class<EventHandler> handlerClass )
	{
		EventHandler h = null;
		for ( EventHandler handler : handlerList )
		{
			if ( handlerClass.isInstance( handler ) )
			{
				h = handler;
				break;
			}
		}
		if ( h != null )
		{
			handlerList.remove( h );
		}
		return h;
	}
}
