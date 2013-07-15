package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationRepository;
import com.gc.mimicry.core.deployment.LocalApplicationRepository;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.core.runtime.SimpleSimulatedNetwork;
import com.gc.mimicry.core.runtime.SimulatedNetwork;
import com.gc.mimicry.core.timing.net.ClockController;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.util.FileNameExtensionFilter;
import com.gc.mimicry.util.IOUtils;
import com.gc.mimicry.util.concurrent.Future;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class Main
{
	private static final Logger	logger;
	static
	{
		logger = LoggerFactory.getLogger( Main.class );
	}

	public static void main( String[] argv ) throws IOException, ResourceException, ScriptException
	{
		Arguments args = parseCmdArguments( argv );
		if ( args == null )
		{
			return;
		}

		logger.info( String.format( "Mimicry v.%s starting...",
				PropertyHelper.getValue( PropertyHelper.MIMICRY_VERSION, "<unknown>" ) ) );

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

		ClassLoadingContext ctx;
		ctx = new ClassLoadingContext( eventHandlerCL );
		for ( File jarFile : aspectJarFiles )
		{
			ctx.addAspectClassPath( jarFile.toURI().toURL() );
			ctx.addBridgeClassPath( jarFile.toURI().toURL() );
		}
		for ( File jarFile : bridgeJarFiles )
		{
			ctx.addBridgeClassPath( jarFile.toURI().toURL() );
		}

		SimulatedNetwork network = new SimpleSimulatedNetwork( ctx );

		network.getEventBroker().addEventListener( new EventListener()
		{

			@Override
			public void handleEvent( Event evt )
			{
				System.out.println( "[]  " + evt );
			}
		} );

		runSimulationScript( args, appRepo, network );

		Future<?> endFuture = network.getSimulationEndFuture();
		endFuture.awaitUninterruptibly( Long.MAX_VALUE );
	}

	private static void runSimulationScript( Arguments args, ApplicationRepository appRepo, SimulatedNetwork network )
			throws IOException, ResourceException, ScriptException
	{
		Binding binding = new Binding();
		binding.setVariable( "network", network );
		binding.setVariable( "repository", appRepo );
		binding.setVariable( "timeline", new ClockController( network.getEventBroker() ) );

		ImportCustomizer importCust = new ImportCustomizer();
		importCust.addStarImports( "com.gc.mimicry.core.deployment" );
		importCust.addStarImports( "com.gc.mimicry.core.runtime" );
		importCust.addStarImports( "com.gc.mimicry.core.timing" );

		String[] roots = new String[] { args.scriptPath };
		GroovyScriptEngine gse = new GroovyScriptEngine( roots, Main.class.getClassLoader() );
		gse.getConfig().addCompilationCustomizers( importCust );
		gse.run( args.mainScript, binding );
	}

	private static Arguments parseCmdArguments( String[] argv )
	{
		Arguments args = new Arguments();
		JCommander commander = new JCommander( args );
		try
		{
			commander.parse( argv );
			return args;
		}
		catch ( ParameterException e )
		{
			commander.usage();
			return null;
		}
	}
}
