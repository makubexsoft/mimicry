package test.integration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;

import org.junit.Test;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Clock;
import com.gc.mimicry.engine.timing.SystemClock;

public class TestApplicationClassLoader
{
    @Test
    public void testRunEmptyApplication() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
            SecurityException
    {
        ClassPathConfiguration classpath = createConfig();
        ApplicationClassLoader loader = ApplicationClassLoader.create(classpath);

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(new EventBridge());

        Application app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
            }
        });
        app.start();
    }

    @Test
    public void testCanStopInifiteLoop() throws Exception
    {
        ClassPathConfiguration clctx = createConfig();
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
                for (;;)
                {
                    System.out.print(new Socket());
                }
            }
        });

        app.start();
        app.stop().awaitUninterruptibly(1000);
    }

    @Test
    public void testLoadsCoreClassesAtStage2() throws Exception
    {
        ClassPathConfiguration clctx = createConfig();
        ClassLoader loader = ApplicationClassLoader.create(clctx);
        EventBridge eventBridge = new EventBridge();

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);

        Class<?> class1 = ctx.getClassLoader().loadClass(Clock.class.getName());
        assertEquals(System.identityHashCode(Clock.class), System.identityHashCode(class1));
    }

    private static ClassPathConfiguration createConfig() throws MalformedURLException
    {
        File bridgeJar = new File("../mimicry-bridge/target/bridge-0.0.1-SNAPSHOT.jar");
        File aspectJar = new File("../mimicry-aspects/target/aspects-0.0.1-SNAPSHOT.jar");

        ClassPathConfiguration ctx = ClassPathConfiguration.createEmpty();
        ctx.addAspectClassPath(aspectJar.toURI().toURL());
        ctx.addBridgeClassPath(aspectJar.toURI().toURL());
        ctx.addBridgeClassPath(bridgeJar.toURI().toURL());

        return ctx;
    }
}