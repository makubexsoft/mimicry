package com.gc.mimicry.ui.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.swing.SwingBuilder;
import groovy.ui.Console;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.RootPaneContainer;
import javax.swing.text.DefaultCaret;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.ext.timing.ClockController;
import com.jidesoft.docking.DockableFrame;

public class ConsoleFrame extends DockableFrame implements RootPaneContainer
{
	private static final long	serialVersionUID	= 3114381363570076039L;

	public ConsoleFrame(final Simulation simu)
	{
		setTitle( "Simulation Console" );

		final Console console = new Console()
		{
			public void newScript( ClassLoader parent, Binding binding )
			{
				setShell( createShell( simu ) );
			};

			@Override
			public void exit( EventObject evt )
			{
				/* suppress */
			}

			@Override
			public void exit()
			{
				/* suppress */
			}

			@Override
			public void fileNewWindow()
			{
				/*
				 * we don't support multiple console instances due to threading
				 * issues
				 */
			}

			@Override
			public void fileNewWindow( EventObject evt )
			{
				/*
				 * we don't support multiple console instances due to threading
				 * issues
				 */
			}
		};

		Map<String, Object> config = new HashMap<String, Object>();
		config.put( "rootContainerDelegate", new Closure<DockableFrame>( this )
		{
			private static final long	serialVersionUID	= -1252898300727076072L;

			@Override
			public DockableFrame call()
			{
				return ConsoleFrame.this;
			}
		} );
		config.put( "menuBarDelegate", new Closure<Void>( this )
		{
			private static final long	serialVersionUID	= 5136110700593205324L;

			@Override
			public Void call( Object... args )
			{
				SwingBuilder builder = console.getSwing();
				setJMenuBar( (JMenuBar) (builder.build( (Class<?>) args[0] )) );
				return null;
			}
		} );

		console.setShowScriptInOutput( false );
		console.setShell( createShell( simu ) );
		console.run( config );

		// enable auto-scroll
		DefaultCaret caret = (DefaultCaret) console.getOutputArea().getCaret();
		caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
	}

	private GroovyShell createShell( final Simulation simu )
	{
		BuiltInBinding binding = new BuiltInBinding();
		binding.defineBuiltInVariable( "simulation", simu );
		binding.defineBuiltInVariable( "timeline", new ClockController( simu.getEventBroker() ) );

		ImportCustomizer importCust = new ImportCustomizer();
		importCust.addStarImports( "com.gc.mimicry.core.deployment" );
		importCust.addStarImports( "com.gc.mimicry.core.runtime" );
		importCust.addStarImports( "com.gc.mimicry.core.timing" );

		CompilerConfiguration config = new CompilerConfiguration();
		config.addCompilationCustomizers( importCust );

		return new GroovyShell( binding, config );
	}
}