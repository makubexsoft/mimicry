package test.com.gc.mimicry.bridge.threading;

import java.io.File;
import java.util.UUID;

import org.junit.Test;
import org.mimicry.ApplicationContext;
import org.mimicry.ClassPathConfiguration;
import org.mimicry.NodeParameters;
import org.mimicry.SimulationParameters;
import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.bundle.LocalApplicationRepository;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.Applications;
import org.mimicry.engine.LocalApplication;
import org.mimicry.engine.LocalEngine;
import org.mimicry.engine.LocalNode;
import org.mimicry.engine.LocalSession;
import org.mimicry.timing.SystemClock;
import org.mimicry.timing.TimelineType;
import org.wso2.siddhi.core.SiddhiManager;


public class TestApplications {

	@Test
	public void testLoadMultipleApplications() throws Exception
	{
		// Global configuration
        LocalApplicationRepository appRepo = new LocalApplicationRepository();
        File workspace = new File("C:/tmp/mimicry");

        // Infrastructure
        CEPEngine eventEngine = new SiddhiCEPEngine();
        LocalEngine engine = new LocalEngine(appRepo, workspace);

        
        SimulationParameters simuParams = new SimulationParameters();
        simuParams.setTimelineType(TimelineType.SYSTEM);
        LocalSession session = engine.createSession(UUID.randomUUID(), simuParams);
        
        
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromSystemClassLoader();
        SystemClock clock = new SystemClock();
        LocalNode node = session.createNode(new NodeParameters("myNode"));

        ClassLoader loader = ApplicationClassLoader.create(config);
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(clock);
        ctx.setEventBridge(node.getEventBridge());
        
        LocalApplication app1 = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                System.out.println("App 1");
            }
        }, eventEngine);
        app1.start(); 
        
        LocalApplication app2 = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                System.out.println("App 2");
            }
        }, eventEngine);
        app2.start(); 
        
        app1.getTerminationFuture().await( 3000 );
        app2.getTerminationFuture().await( 3000 );
	}
}
