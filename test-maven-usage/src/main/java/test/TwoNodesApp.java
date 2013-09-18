package test;

import java.net.Socket;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.local.Applications;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;

public class TwoNodesApp
{

	public static void main( String[] args ) throws Exception
	{
		ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
		
		// per NODE
		//
		EventBridge eventBridge = new EventBridge();
		eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(Event evt)
            {
                System.out.println("[event] " + evt);
            }
        });

		// per APPLICATION
		//
        ClassLoader loader = ApplicationClassLoader.create(config, ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);
        LocalApplication app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
            	System.out.print(new Socket());
            }
        });
        
        // the SCRIPT
        //
        System.out.println("starting...");
        app.start();
        Thread.sleep(3000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
		
		

	}

}
