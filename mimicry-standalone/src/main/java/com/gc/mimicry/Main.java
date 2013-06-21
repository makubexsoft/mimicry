package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.runtime.ApplicationRef;
import com.gc.mimicry.core.runtime.NodeConfiguration;
import com.gc.mimicry.core.runtime.NodeRef;
import com.gc.mimicry.core.runtime.SimulatedNetwork;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockType;
import com.gc.mimicry.util.concurrent.Future;

public class Main
{
	public static void main( String[] argv ) throws IOException, ResourceException, ScriptException
	{
		Arguments args = parseCmdArguments( argv );
		if(args == null)
		{
			return;
		}

		// 1. Setup loopback event communication
		SimulatedNetwork network = new SimulatedNetwork()
		{
			
			@Override
			public NodeRef spawnNode( NodeConfiguration nodeConfig )
			{
				System.out.println("Node: " + nodeConfig);
				return null;
			}

			@Override
			public ApplicationRef spawnApplication( NodeRef node, ApplicationDescriptor appDesc )
			{
				System.out.println("App: " + appDesc);
				return null;
			}

			@Override
			public Clock installClock( ClockType type )
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void startApplication( ApplicationRef app )
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public Future<?> shutdown()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Future<?> getSimulationEndFuture()
			{
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		// 2. Setup Bindings
		Binding binding = new Binding();
		binding.setVariable( "network", network );
		
		ImportCustomizer importCust = new ImportCustomizer();
		importCust.addStarImports( "com.gc.mimicry.core.deployment" );
		importCust.addStarImports( "com.gc.mimicry.core.runtime" );
		importCust.addStarImports( "com.gc.mimicry.core.timing" );

		// 3. Read infrastructure / script file
		String[] roots = new String[] { args.scriptPath };
		GroovyScriptEngine gse = new GroovyScriptEngine( roots, Main.class.getClassLoader() );
		gse.getConfig().addCompilationCustomizers( importCust );
		gse.run( args.mainScript, binding );

		// 4. Run until all applications are shutdown
		Future<?> endFuture = network.getSimulationEndFuture();
		endFuture.awaitUninterruptibly( Long.MAX_VALUE );
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
