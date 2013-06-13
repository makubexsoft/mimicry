package com.gc.mimicry.bridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class WeavingClassLoader extends WeavingURLClassLoader
{

	public WeavingClassLoader(Collection<URL> classPath, Collection<URL> aspects,
			LoopInterceptingByteCodeLoader loader, ClassLoader parent) throws MalformedURLException
	{
		super( classPath.toArray( new URL[0] ), aspects.toArray( new URL[0] ), parent );

		Preconditions.checkNotNull( aspects );
		Preconditions.checkNotNull( loader );
		this.loader = loader;
	}

	@Override
	protected byte[] getBytes( String name ) throws IOException
	{
		byte[] byteCode = loader.loadTransformedByteCode( name );
		if ( byteCode == null )
		{
			byteCode = super.getBytes( name );
			if ( byteCode != null )
			{
				logger.debug( "AspectJ loaded byte code: " + name + " (" + byteCode.length + " bytes)" );
			}
		}
		else if ( logger.isDebugEnabled() )
		{
			logger.debug( "Soot loaded byte code: " + name + " (" + byteCode.length + " bytes)" );
		}
		return byteCode;
	}

	private final LoopInterceptingByteCodeLoader	loader;
	private static Logger							logger;
	static
	{
		logger = LoggerFactory.getLogger( WeavingClassLoader.class );
	}
}
