package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.util.UUID;

import javax.swing.UIManager;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.cep.CEPEngineFactory;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngineFactory;
import com.gc.mimicry.engine.AlwaysFirstNodeStrategy;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.local.LocalEngine;
import com.gc.mimicry.engine.local.LocalSession;
import com.gc.mimicry.engine.remote.EngineAdvertiser;
import com.gc.mimicry.engine.remote.EngineExporter;
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
		CEPEngineFactory engineFactory = new SiddhiCEPEngineFactory();

		// Bootstrap Engine
		LocalEngine engine = new LocalEngine( appRepo, workspace, engineFactory );

		EngineAdvertiser advertiser = EngineExporter.exportEngine( engine );
		advertiser.start();

		// Configure Simulation
		SimulationParameters simuParams = new SimulationParameters();
		simuParams.setTimelineType( TimelineType.SYSTEM );

		// Start Simulation
		UUID simulationId = UUID.randomUUID();

		LocalSession localSession = engine.createSession( simulationId, simuParams );

		Simulation.Builder builder = new Simulation.Builder();
		builder.withNodeDistributionStrategy( new AlwaysFirstNodeStrategy() );
		builder.withEventEngine( localSession.getEventEngine() );
		builder.withSimulationParameters( simuParams );
		builder.addSession( localSession );
		Simulation simu = builder.build();

		// Run User-Script
		runSimulationScript( args, appRepo, simu );

		// Wait for end of Simulation
		Future<?> endFuture = simu.getSimulationEndFuture();
		endFuture.awaitUninterruptibly( Long.MAX_VALUE );
	}

	private static void runSimulationScript( Arguments args, ApplicationRepository appRepo, Simulation network )
			throws Exception
	{
		ClockController clockController = new ClockController( network.getEventEngine() );

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
