package test;

import java.net.Socket;

import org.wso2.siddhi.core.SiddhiManager;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngine;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.ApplicationEvent;
import com.gc.mimicry.engine.local.Applications;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;

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
