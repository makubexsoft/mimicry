package test.com.gc.mimicry.bridge.threading;

import java.io.File;
import java.util.UUID;

import org.junit.Test;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.NodeParameters;
import com.gc.mimicry.engine.SimpleEventBroker;
import com.gc.mimicry.engine.SimulationParameters;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.engine.local.Applications;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.local.LocalEngine;
import com.gc.mimicry.engine.local.LocalNode;
import com.gc.mimicry.engine.local.LocalSession;
import com.gc.mimicry.engine.timing.SystemClock;
import com.gc.mimicry.engine.timing.TimelineType;

public class TestApplications {

	@Test
	public void testLoadMultipleApplications() throws Exception
	{
		// Global configuration
        LocalApplicationRepository appRepo = new LocalApplicationRepository();
        File workspace = new File("C:/tmp/mimicry");

        // Infrastructure
        SimpleEventBroker broker = new SimpleEventBroker();
        LocalEngine engine = new LocalEngine(broker, appRepo, workspace);

        
        SimulationParameters simuParams = new SimulationParameters();
        simuParams.setTimelineType(TimelineType.SYSTEM);
        LocalSession session = engine.createSession(UUID.randomUUID(), simuParams);
        
        
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
        SystemClock clock = new SystemClock();
        LocalNode node = session.createNode(new NodeParameters("myNode"));

        ClassLoader loader = ApplicationClassLoader.create(config, TestThreadScheduler.class.getClassLoader());
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
        });
        app1.start(); 
        
        LocalApplication app2 = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                System.out.println("App 2");
            }
        });
        app2.start(); 
        
        app1.getTerminationFuture().await( 3000 );
        app2.getTerminationFuture().await( 3000 );
	}
}
