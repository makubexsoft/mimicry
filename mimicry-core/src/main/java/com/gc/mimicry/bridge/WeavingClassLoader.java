package com.gc.mimicry.bridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * {@link ClassLoader} implementation that uses the
 * {@link LoopInterceptingByteCodeLoader} for loading and pre-processing byte
 * code of a simulated application. After the byte code has been pre-processed
 * it's forwarded to aspectJ in order to apply more aspects.
 * 
 * @author Marc-Christian Schulze
 * 
 */
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

	/**
	 * First queries the {@link LoopInterceptingByteCodeLoader} to load the byte
	 * code if it doesn't find the class (because it's not part of simulated
	 * application) the call is forwarded to the actual implementation of the
	 * base class.
	 */
	@Override
	protected byte[] getBytes( String name ) throws IOException
	{
		byte[] byteCode = null; // FIXME: loader.loadTransformedByteCode( name
								// );
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
		// IOUtils.writeToFile(byteCode, new File("_dump_" + name + ".class"));
		return byteCode;
	}

	static
	{
		logger = LoggerFactory.getLogger( WeavingClassLoader.class );
	}
	private final LoopInterceptingByteCodeLoader	loader;
	private static Logger							logger;
}
