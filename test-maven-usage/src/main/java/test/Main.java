package test;

import java.net.Socket;

import org.mimicry.ApplicationContext;
import org.mimicry.ClassPathConfiguration;
import org.mimicry.EventListener;
import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.Applications;
import org.mimicry.engine.EventBridge;
import org.mimicry.engine.LocalApplication;
import org.mimicry.timing.SystemClock;
import org.wso2.siddhi.core.SiddhiManager;


public class Main
{
	public static void main( String[] args ) throws Exception
	{
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromSystemClassLoader();
		CEPEngine eventEngine = new SiddhiCEPEngine();
		
		// per NODE
		//
		EventBridge eventBridge = new EventBridge(eventEngine);
		eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(ApplicationEvent evt)
            {
                System.out.println("[event] " + evt);
            }
        });
		
		SiddhiManager siddhi = new SiddhiManager();

		// per APPLICATION
		//
        ClassLoader loader = ApplicationClassLoader.create(config);
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        LocalApplication app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
            	for(int i = 0; i < 10; ++i)
            	System.out.print(new Socket());
            }
        }, eventEngine);
        
        // the SCRIPT
        //
        System.out.println("starting...");
        app.start();
        Thread.sleep(3000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
	}
}
