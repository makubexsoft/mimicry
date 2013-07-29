package com.gc.mimicry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyHelper
{
	private static final Logger		logger;
	private static final File		PROPERTIES_FILE		= new File( "./mimicry.properties" );

	public static String			MIMICRY_VERSION		= "mimicry.version";
	public static String			MIMICRY_SCRIPT_PATH	= "mimicry.script.path";
	public static String			MIMICRY_BRIDGE_PATH	= "mimicry.bridge.path";
	public static String			MIMICRY_CORE_PATH	= "mimicry.core.path";
	public static String			MIMICRY_ASPECT_PATH	= "mimicry.aspect.path";
	public static String			MIMICRY_PLUGIN_PATH	= "mimicry.plugin.path";

	private static final Properties	properties;
	static
	{
		logger = LoggerFactory.getLogger( PropertyHelper.class );
		properties = new Properties();
		try
		{
			properties.load( new BufferedInputStream( new FileInputStream( PROPERTIES_FILE ) ) );
		}
		catch ( FileNotFoundException e )
		{
			logger.info( "No mimicry.properties file found. Using defaults.", e );
		}
		catch ( IOException e )
		{
			logger.error( "Failed to parse mimicry.properties file.", e );
		}
	}

	public static String getValue( String name, String defaultValue )
	{
		String value = properties.getProperty( name );
		if ( value == null )
		{
			value = defaultValue;
		}
		return value;
	}
}
