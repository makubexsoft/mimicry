package org.mimicry.junit;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mimicry.Simulation;
import org.mimicry.bundle.ApplicationRepository;
import org.mimicry.bundle.LocalApplicationRepository;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.CEPEngineFactory;
import org.mimicry.cep.siddhi.SiddhiCEPEngineFactory;
import org.mimicry.engine.LocalEngine;

@RunWith(MimicryTestRunner.class)
public class MimicryTestCase
{
	private LocalEngine					localEngine;
	private File						workspace;
	private LocalApplicationRepository	applicationRepository;
	private Simulation					simulation;

	@Before
	public void setUp() throws IOException
	{
		// Global configuration
		applicationRepository = new LocalApplicationRepository();
		setUpWorkspace();
		CEPEngineFactory engineFactory = new SiddhiCEPEngineFactory();

		// Bootstrap Engine
		localEngine = new LocalEngine( applicationRepository, workspace, engineFactory );
	}

	private void setUpWorkspace()
	{
		EngineConfiguration annotation = getClass().getAnnotation( EngineConfiguration.class );
		String workspacePath = "/tmp/mimicry";
		if ( annotation != null )
		{
			annotation.workspace();
		}
		workspace = new File( workspacePath );
	}

	protected LocalEngine getLocalEngine()
	{
		return localEngine;
	}

	protected File getWorkspace()
	{
		return workspace;
	}

	protected CEPEngine getEventEngine()
	{
		return simulation.getEventEngine();
	}

	protected ApplicationRepository getApplicationRepository()
	{
		return applicationRepository;
	}

	protected Simulation getSimulation()
	{
		return simulation;
	}

	void setSimulation( Simulation simulation )
	{
		this.simulation = simulation;
	}
}
