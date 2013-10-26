package test;

import java.net.Socket;

import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.ApplicationContext;
import org.mimicry.engine.ClassPathConfiguration;
import org.mimicry.engine.EventListener;
import org.mimicry.engine.event.ApplicationEvent;
import org.mimicry.engine.local.Applications;
import org.mimicry.engine.local.LocalApplication;
import org.mimicry.engine.stack.EventBridge;
import org.mimicry.engine.timing.SystemClock;
import org.wso2.siddhi.core.SiddhiManager;


public class CheckpointExample
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
            	{
            		try
					{
						Thread.sleep( 20 );
					}
					catch ( InterruptedException e )
					{
						e.printStackTrace();
					}
            		System.out.print(new Socket());
            	}
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
