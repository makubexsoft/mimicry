package test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.MimicryConfiguration;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;
import com.gc.mimicry.util.ClassPathUtil;

public class Main
{
	public static void main( String[] args ) throws Exception
	{
		MimicryConfiguration clctx = createConfig();
        ClassLoader loader = ApplicationClassLoader.create(clctx);
        EventBridge eventBridge = new EventBridge();

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);

        Application app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
            	System.out.print(new Socket());
//                for (;;)
//                {
//                    System.out.print("test");
//                }
            }
        });

        eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(Event evt)
            {
                System.out.println("[event] " + evt);
            }
        });

        System.out.println("starting...");
        app.start();
        Thread.sleep(3000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
	}
	
	private static MimicryConfiguration createConfig() throws MalformedURLException
    {
        ClassLoader loader = Main.class.getClassLoader();

        File bridgeJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/SimulatorBridge.class"));
        File aspectJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/bridge/aspects/ConsoleAspect.class"));
        File coreJar = new File(ClassPathUtil.getResourceLocation(loader,
                "com/gc/mimicry/engine/Application.class"));
        
        MimicryConfiguration ctx = new MimicryConfiguration(loader);
        ctx.addAspectClassPath(aspectJar.toURI().toURL());
        ctx.addBridgeClassPath(bridgeJar.toURI().toURL());
        ctx.addCoreClassPath( coreJar.toURI().toURL() );

        return ctx;
    }
}
