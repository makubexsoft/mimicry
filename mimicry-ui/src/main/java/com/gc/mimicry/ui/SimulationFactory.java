package com.gc.mimicry.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gc.mimicry.engine.MimicryConfiguration;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.StandaloneSimulation;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.util.FileNameExtensionFilter;
import com.gc.mimicry.util.IOUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class SimulationFactory
{
	private SimulationFactory()
	{
	}
	
	public static SimulationFactory getDefault()
	{
		return new SimulationFactory();
	}
	
	public Simulation createSimulation() throws IOException
	{
		ApplicationRepository appRepo = new LocalApplicationRepository();

		File bridgeDir = new File( PropertyHelper.getValue( PropertyHelper.MIMICRY_BRIDGE_PATH, "." ) );
		List<File> bridgeJarFiles = IOUtils.collectFiles( bridgeDir, new FileNameExtensionFilter( ".jar" ) );

		File aspectDir = new File( PropertyHelper.getValue( PropertyHelper.MIMICRY_ASPECT_PATH, "." ) );
		List<File> aspectJarFiles = IOUtils.collectFiles( aspectDir, new FileNameExtensionFilter( ".jar" ) );

		File sharedDir = new File( PropertyHelper.getValue( PropertyHelper.MIMICRY_SHARED_PATH, "." ) );
		List<File> sharedJarFiles = IOUtils.collectFiles( sharedDir, new FileNameExtensionFilter( ".jar" ) );

		File coreDir = new File( PropertyHelper.getValue( PropertyHelper.MIMICRY_CORE_PATH, "." ) );
		List<File> coreJarFiles = IOUtils.collectFiles( coreDir, new FileNameExtensionFilter( ".jar" ) );

		File pluginDir = new File( PropertyHelper.getValue( PropertyHelper.MIMICRY_PLUGIN_PATH, "." ) );
		List<File> pluginJarFiles = IOUtils.collectFiles( pluginDir, new FileNameExtensionFilter( ".jar" ) );

		List<File> tmp = new ArrayList<File>();
		tmp.addAll( sharedJarFiles );
		tmp.addAll( coreJarFiles );
		tmp.addAll( pluginJarFiles );
		Collection<URL> urls = Collections2.transform( tmp, new Function<File, URL>()
		{
			@Override
			public URL apply( File f )
			{
				try
				{
					return f.toURI().toURL();
				}
				catch ( MalformedURLException e )
				{
					return null;
				}
			}
		} );

		URLClassLoader eventHandlerCL = new URLClassLoader( urls.toArray( new URL[0] ), Main.class.getClassLoader() );

		MimicryConfiguration ctx;
		ctx = new MimicryConfiguration( eventHandlerCL );
		for ( File jarFile : aspectJarFiles )
		{
			ctx.addAspectClassPath( jarFile.toURI().toURL() );
			ctx.addBridgeClassPath( jarFile.toURI().toURL() );
		}
		for ( File jarFile : bridgeJarFiles )
		{
			ctx.addBridgeClassPath( jarFile.toURI().toURL() );
		}

		return new StandaloneSimulation( ctx );
	}
}
