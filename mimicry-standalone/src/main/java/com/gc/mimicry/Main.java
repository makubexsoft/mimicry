package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

import javax.swing.UIManager;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.engine.AlwaysFirstNodeStrategy;
import com.gc.mimicry.engine.Session;
import com.gc.mimicry.engine.SimpleEventBroker;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.event.DefaultEventFactory;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.engine.event.Identity;
import com.gc.mimicry.engine.local.LocalEngine;
import com.gc.mimicry.engine.timing.TimelineType;
import com.gc.mimicry.ext.timing.ClockController;
import com.gc.mimicry.util.concurrent.Future;

public class Main
{
	private static final Logger	logger;
	static
	{
		logger = LoggerFactory.getLogger( Main.class );
	}

	public static void main( String[] argv ) throws Exception
	{
		Arguments args = parseCmdArguments( argv );
		if ( args == null )
		{
			return;
		}

		logger.info( String.format( "Mimicry v.%s starting...",
				PropertyHelper.getValue( PropertyHelper.MIMICRY_VERSION, "<unknown>" ) ) );

		setLaF();

		// Global configuration
		LocalApplicationRepository appRepo = new LocalApplicationRepository();
		File workspace = new File( "C:/tmp/mimicry" );

		// Infrastructure
		SimpleEventBroker broker = new SimpleEventBroker();
		LocalEngine engine = new LocalEngine( broker, appRepo, workspace );

		// Simulation specific configuration
		SimulationParameters simuParams = new SimulationParameters();
		simuParams.setTimelineType( TimelineType.SYSTEM );

		// Setup
		HashSet<Session> sessions = new HashSet<Session>();
		sessions.add( engine.createSession( UUID.randomUUID(), simuParams ) );
		Simulation simu = new Simulation( sessions, new AlwaysFirstNodeStrategy() );

		runSimulationScript( args, appRepo, simu );

		Future<?> endFuture = simu.getSimulationEndFuture();
		endFuture.awaitUninterruptibly( Long.MAX_VALUE );
	}

	private static void runSimulationScript( Arguments args, ApplicationRepository appRepo, Simulation network )
			throws Exception
	{
		EventFactory eventFactory = DefaultEventFactory.create( Identity.create( "Simulation-Script" ) );
		ClockController clockController = new ClockController( network.getEventEngine(), eventFactory );

		Binding binding = new Binding();
		binding.setVariable( "simulation", network );
		binding.setVariable( "repository", appRepo );
		binding.setVariable( "timeline", clockController );

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
