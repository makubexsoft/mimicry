package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.wso2.siddhi.core.SiddhiManager;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngine;
import com.gc.mimicry.engine.AlwaysFirstNodeStrategy;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.Node;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.local.LocalEngine;
import com.gc.mimicry.engine.local.LocalSession;
import com.gc.mimicry.engine.stack.EventHandlerParameters;
import com.gc.mimicry.engine.timing.TimelineType;
import com.gc.mimicry.util.IOUtils;

public class TestSetup {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		// Global configuration
		LocalApplicationRepository appRepo = new LocalApplicationRepository();
		File workspace = new File("C:/tmp/mimicry");
		
		// Install app to local repository
		byte[] bundleContent = IOUtils.readIntoByteArray(new File("src/main/resources/ping-server.zip"));
		appRepo.storeBundle("ping-server", new ByteArrayInputStream(bundleContent));
		bundleContent = IOUtils.readIntoByteArray(new File("src/main/resources/ping-client.zip"));
		appRepo.storeBundle("ping-client", new ByteArrayInputStream(bundleContent));
		
		// Infrastructure
		LocalEngine engine = new LocalEngine( appRepo, workspace);

		// Simulation specific configuration
		SimulationParameters simuParams = new SimulationParameters();
		simuParams.setTimelineType(TimelineType.SYSTEM);
		
		LocalSession localSession = engine.createSession(UUID.randomUUID(), simuParams);

		// Setup
		Simulation.Builder builder = new Simulation.Builder();
        builder.withNodeDistributionStrategy(new AlwaysFirstNodeStrategy());
        builder.withEventEngine(localSession.getEventEngine());
        builder.withSimulationParameters(simuParams);
		builder.addSession(localSession);
        Simulation simu = builder.build();

		// Simulation
		NodeParameters params = new NodeParameters("MyNode-1");
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.PortManager"));
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.tcp.SimpleTCPDataTransport"));
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.tcp.TCPConnectionManager"));
		
		Node node1 = simu.createNode(params);
		Application server = node1.installApplication("ping-server", "/apps/server");
		
		params.setNodeName("MyNode-2");
		Node node2 = simu.createNode(params);
		Application client = node2.installApplication("ping-client", "/apps/client");
		
		server.start("8000");
		client.start("127.0.0.1", "8000");
		
		server.getTerminationFuture().awaitUninterruptibly(50000);
	}
}
