package com.gc.mimicry;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationRepository;
import com.gc.mimicry.core.deployment.LocalApplicationRepository;
import com.gc.mimicry.core.runtime.SimpleSimulatedNetwork;
import com.gc.mimicry.core.runtime.SimulatedNetwork;
import com.gc.mimicry.util.concurrent.Future;

public class Main {
	public static void main(String[] argv) throws IOException,
			ResourceException, ScriptException {
		Arguments args = parseCmdArguments(argv);
		if (args == null) {
			return;
		}

		ApplicationRepository appRepo = new LocalApplicationRepository();

		
		String BRIDGE_PATH = "../mimicry-bridge/target/classes";
	    String ASPECTS_PATH = "../mimicry-aspects/target/classes";
	     
	    URLClassLoader eventHandlerCL = new URLClassLoader(new URL[]{new File("../mimicry-plugin-core/target/classes").toURI().toURL()}, Main.class.getClassLoader()); 
	    
		// 1. Setup loopback event communication
		ClassLoadingContext ctx;
		ctx = new ClassLoadingContext(eventHandlerCL);
		ctx.addAspectClassPath(new File(ASPECTS_PATH).toURI().toURL());

		ctx.addBridgeClassPath(new File(ASPECTS_PATH).toURI().toURL());
		ctx.addBridgeClassPath(new File(BRIDGE_PATH).toURI().toURL());

		SimulatedNetwork network = new SimpleSimulatedNetwork(ctx);

		// 2. Setup Bindings
		Binding binding = new Binding();
		binding.setVariable("network", network);
		binding.setVariable("repository", appRepo);

		ImportCustomizer importCust = new ImportCustomizer();
		importCust.addStarImports("com.gc.mimicry.core.deployment");
		importCust.addStarImports("com.gc.mimicry.core.runtime");
		importCust.addStarImports("com.gc.mimicry.core.timing");

		// 3. Read infrastructure / script file
		String[] roots = new String[] { args.scriptPath };
		GroovyScriptEngine gse = new GroovyScriptEngine(roots,
				Main.class.getClassLoader());
		gse.getConfig().addCompilationCustomizers(importCust);
		gse.run(args.mainScript, binding);

		// 4. Run until all applications are shutdown
		Future<?> endFuture = network.getSimulationEndFuture();
		endFuture.awaitUninterruptibly(Long.MAX_VALUE);
	}

	private static Arguments parseCmdArguments(String[] argv) {
		Arguments args = new Arguments();
		JCommander commander = new JCommander(args);
		try {
			commander.parse(argv);
			return args;
		} catch (ParameterException e) {
			commander.usage();
			return null;
		}
	}
}
