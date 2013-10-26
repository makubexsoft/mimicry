package org.mimicry.junit;

import java.util.UUID;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.mimicry.engine.AlwaysFirstNodeStrategy;
import org.mimicry.engine.Simulation;
import org.mimicry.engine.SimulationParameters;
import org.mimicry.engine.local.LocalSession;


public class MimicryTestRunner extends BlockJUnit4ClassRunner
{
	public MimicryTestRunner(Class<?> clazz) throws InitializationError
	{
		super( clazz );
	}

	@Override
	protected Statement methodInvoker( FrameworkMethod method, Object test )
	{
		Statement nextStatement = super.methodInvoker( method, test );
		if ( test instanceof MimicryTestCase )
		{
			MimicryTestCase testCase = (MimicryTestCase) test;
			return new CreateSimulationStatement( testCase, method, nextStatement );
		}
		else
		{
			return nextStatement;
		}
	}
}

class CreateSimulationStatement extends Statement
{
	private MimicryTestCase	testCase;
	private FrameworkMethod	method;
	private Statement		nextStatement;

	public CreateSimulationStatement(MimicryTestCase testCase, FrameworkMethod method, Statement nextStatement)
	{
		this.testCase = testCase;
		this.method = method;
		this.nextStatement = nextStatement;
	}

	@Override
	public void evaluate() throws Throwable
	{
		createSimulation();
		nextStatement.evaluate();
	}

	private void createSimulation()
	{
		SimulationParameters simuParams = getSimulationParameters( method );

		UUID simulationId = UUID.randomUUID();

		LocalSession localSession = testCase.getLocalEngine().createSession( simulationId, simuParams );

		Simulation.Builder builder = new Simulation.Builder();
		builder.withNodeDistributionStrategy( new AlwaysFirstNodeStrategy() );
		builder.withEventEngine( localSession.getEventEngine() );
		builder.withSimulationParameters( simuParams );
		builder.addSession( localSession );
		Simulation simulation = builder.build();

		testCase.setSimulation( simulation );
	}

	private SimulationParameters getSimulationParameters( FrameworkMethod method )
	{
		return new SimulationParameters();
	}
}