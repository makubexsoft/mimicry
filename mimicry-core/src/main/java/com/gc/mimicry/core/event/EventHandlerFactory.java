package com.gc.mimicry.core.event;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.core.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;
import com.google.common.base.Preconditions;

public class EventHandlerFactory
{
	private static final Logger				logger;
	static
	{
		logger = LoggerFactory.getLogger( EventHandlerFactory.class );
	}

	private Scheduler						scheduler;
	private Clock							clock;
	private SessionLocalMessagingService	messaging;

	public EventHandlerFactory(Scheduler scheduler, Clock clock, SessionLocalMessagingService messaging)
	{
		Preconditions.checkNotNull( scheduler );
		Preconditions.checkNotNull( clock );
		Preconditions.checkNotNull( messaging );
		this.scheduler = scheduler;
		this.clock = clock;
		this.messaging = messaging;
	}

	public EventHandler create( String fullQualifiedName, ClassLoader classLoader )
	{
		Class<EventHandler> clazz = loadClass( classLoader, fullQualifiedName );
		if ( clazz == null )
		{
			return null;
		}
		// Constructor<EventHandler> ctor = findConstructor( clazz );
		// if ( ctor == null ) {
		// return null;
		// }

		// return instantiate( ctor );
		try
		{
			return clazz.newInstance();
		}
		catch ( Exception e )
		{
			logger.error( "Failed to instantiate event handler: " + clazz.getName(), e );
			return null;
		}
	}

	private EventHandler instantiate( Constructor<EventHandler> ctor )
	{
		try
		{
			return ctor.newInstance( scheduler, clock, messaging );
		}
		catch ( Exception e )
		{
			logger.error( "Failed to instantiate event handler: " + ctor.getDeclaringClass().getName(), e );
		}
		return null;
	}

	@SuppressWarnings(
	{ "unchecked" })
	private Class<EventHandler> loadClass( ClassLoader classLoader, String name )
	{
		try
		{
			Class<?> loadedClass = classLoader.loadClass( name );
			if ( !EventHandler.class.isAssignableFrom( loadedClass ) )
			{
				return null;
			}
			return (Class<EventHandler>) loadedClass;
		}
		catch ( ClassNotFoundException e )
		{
			logger.error( "Failed to load event handler class: " + name, e );
			return null;
		}
	}

	private Constructor<EventHandler> findConstructor( Class<EventHandler> clazz )
	{
		try
		{
			return clazz.getConstructor( Scheduler.class, Clock.class, SessionLocalMessagingService.class );
		}
		catch ( Exception e )
		{
			logger.error( "Failed to instantiate event handler: " + clazz.getName(), e );
		}
		return null;
	}
}
