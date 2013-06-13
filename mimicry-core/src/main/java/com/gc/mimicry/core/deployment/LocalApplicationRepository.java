package com.gc.mimicry.core.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.util.IOUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class LocalApplicationRepository implements ApplicationRepository
{

	private static final Logger							logger;
	private static final String							APPLICATION_PROPERTIES;
	static
	{
		logger = LoggerFactory.getLogger( LocalApplicationRepository.class );
		APPLICATION_PROPERTIES = "application.properties";
	}

	private final Map<String, ApplicationDescriptor>	loadedDescriptors;
	private final File									repositoryPath;

	public LocalApplicationRepository(File repositoryPath)
	{
		Preconditions.checkNotNull( repositoryPath );
		this.repositoryPath = repositoryPath;
		loadedDescriptors = new HashMap<String, ApplicationDescriptor>();
	}

	@Override
	public Set<String> getApplicationNames()
	{
		Set<String> appNames = new HashSet<String>();
		for ( File f : repositoryPath.listFiles() )
		{
			if ( f.getName().endsWith( ".zip" ) )
			{
				appNames.add( f.getName().substring( 0, f.getName().length() - 4 ) );
			}
		}
		return appNames;
	}

	@Override
	public ApplicationDescriptor getApplicationDescriptor( String applicationName )
	{
		ApplicationDescriptor descriptor = loadedDescriptors.get( applicationName );
		if ( descriptor == null )
		{
			try
			{
				descriptor = loadDescriptor( applicationName );
				loadedDescriptors.put( applicationName, descriptor );
			}
			catch ( IOException e )
			{
				logger.warn( "Failed to load the application descriptor of " + applicationName, e );
			}
		}
		return descriptor;
	}

	private ApplicationDescriptor loadDescriptor( String appName ) throws IOException
	{
		File bundleFile = new File( repositoryPath, appName + ".zip" );
		final ZipFile zipFile = new ZipFile( bundleFile );
		try
		{
			ZipEntry entry = zipFile.getEntry( APPLICATION_PROPERTIES );
			if ( entry == null )
			{
				throw new IOException( "Application bundle doesn't contain a " + APPLICATION_PROPERTIES );
			}
			Properties props = new Properties();
			props.load( zipFile.getInputStream( entry ) );

			ApplicationDescriptorBuilder builder = ApplicationDescriptorBuilder.newDescriptor( appName );
			builder.withBundleLocation( bundleFile );
			builder.withMainClass( props.getProperty( "Main-Class" ) );
			builder.withRunnableJar( props.getProperty( "Runnable-Jar" ) );
			builder.withCommandLine( props.getProperty( "Command-Line" ) );
			builder.withClassPath( props.getProperty( "Class-Path" ) );
			for ( String s : Splitter.on( "," ).split( props.getProperty( "Supported-OS" ) ) )
			{
				builder.withSupportedOS( s );
			}
			return builder.build();
		}
		finally
		{
			IOUtils.closeSilently( new Closeable() { public void close() throws IOException { zipFile.close(); } } );
		}
	}

	@Override
	public void storeApplication( String appName, InputStream bundleStream ) throws IOException
	{
		File bundleFile = new File( repositoryPath, appName + ".zip" );
		if ( bundleFile.exists() )
		{
			throw new IOException( "Application already present in repository at " + bundleFile );
		}
		bundleFile.createNewFile();
		IOUtils.writeToFile( bundleStream, bundleFile );
	}
}
