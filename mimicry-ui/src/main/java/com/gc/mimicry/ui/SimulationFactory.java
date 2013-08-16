package com.gc.mimicry.ui;

import java.io.IOException;

import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.StandaloneSimulation;

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
		return new StandaloneSimulation( ClassPathConfiguration.deriveFromClassPath() );
	}
}
