package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import com.gc.mimicry.engine.AlwaysFirstNodeStrategy;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.Node;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.Session;
import com.gc.mimicry.engine.SimpleEventBroker;
import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.local.LocalEngine;
import com.gc.mimicry.engine.stack.EventHandlerParameters;
import com.gc.mimicry.engine.timing.TimelineType;
import com.gc.mimicry.ext.stdio.events.ConsoleOutputEvent;
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
		SimpleEventBroker broker = new SimpleEventBroker();
		broker.addEventListener(new EventListener() {
			
			@Override
			public void handleEvent(Event evt) {
				if(evt instanceof ConsoleOutputEvent)
				{
					ConsoleOutputEvent e = (ConsoleOutputEvent)evt;
					System.out.print("[console] " + new String(e.getData()));
				}
				else
				{
					//System.out.println("[event] " + evt);
				}
			}
		});
		LocalEngine engine = new LocalEngine(broker, appRepo, workspace);

		// Simulation specific configuration
		SimulationParameters simuParams = new SimulationParameters();
		simuParams.setTimelineType(TimelineType.SYSTEM);
		
		// Setup
		HashSet<Session> sessions = new HashSet<Session>();
		sessions.add(engine.createSession(UUID.randomUUID(), simuParams));
		Simulation simu = new Simulation(sessions, new AlwaysFirstNodeStrategy());

		// Simulation
		NodeParameters params = new NodeParameters("MyNode-1");
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.PortManager"));
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.tcp.SimpleTCPDataTransport"));
		params.getEventStack().add(new EventHandlerParameters("com.gc.mimicry.plugin.net.tcp.TCPConnectionManager"));
		
		Node node1 = simu.createNode(params);
		LocalApplication server = node1.installApplication("ping-server", "/apps/server");
		
		params.setNodeName("MyNode-2");
		Node node2 = simu.createNode(params);
		LocalApplication client = node2.installApplication("ping-client", "/apps/client");
		
		server.start("8000");
		client.start("127.0.0.1", "8000");
		
		server.getTerminationFuture().awaitUninterruptibly(50000);
	}
}
