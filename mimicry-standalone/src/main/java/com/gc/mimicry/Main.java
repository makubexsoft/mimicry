package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;

import javax.swing.UIManager;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.StandaloneSimulation;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.ext.timing.ClockController;
import com.gc.mimicry.util.concurrent.Future;

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

		setLaF();

		// Create context
		ClassPathConfiguration ctx = ClassPathConfiguration.deriveFromClassPath();

		// Create simulation
		Simulation network = new StandaloneSimulation( ctx );

		ApplicationRepository appRepo = new LocalApplicationRepository();
		runSimulationScript( args, appRepo, network );

		Future<?> endFuture = network.getSimulationEndFuture();
		endFuture.awaitUninterruptibly( Long.MAX_VALUE );
	}

	private static void runSimulationScript( Arguments args, ApplicationRepository appRepo, Simulation network )
			throws IOException, ResourceException, ScriptException
	{
		Binding binding = new Binding();
		binding.setVariable( "simulation", network );
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

	private static void setLaF()
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch ( Exception e )
		{
			logger.warn( "Failed to set look-and-feel.", e );
		}
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
