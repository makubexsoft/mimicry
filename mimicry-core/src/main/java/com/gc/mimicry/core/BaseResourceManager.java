package com.gc.mimicry.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of a {@link ResourceManager} meant to be sub-classed or
 * used in composition for convenience.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class BaseResourceManager implements ResourceManager
{
	private static final Logger						logger;
	static
	{
		logger = LoggerFactory.getLogger( BaseResourceManager.class );
	}
	private final CopyOnWriteArrayList<Closeable>	resources;

	public BaseResourceManager()
	{
		resources = new CopyOnWriteArrayList<Closeable>();
	}

	@Override
	public void attachResource( Closeable res )
	{
		resources.add( res );
	}

	@Override
	public boolean detachResource( Closeable res )
	{
		return resources.remove( res );
	}

	@Override
	public void close()
	{
		Collections.reverse( resources );
		for ( Closeable resource : resources )
		{
			try
			{
				resource.close();
			}
			catch ( IOException e )
			{
				logger.error( "Failed to close resource: " + resource, e );
			}
		}
	}
}
