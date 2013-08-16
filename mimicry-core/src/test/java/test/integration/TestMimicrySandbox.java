package test.integration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Clock;
import com.gc.mimicry.engine.timing.SystemClock;

public class TestMimicrySandbox
{
    public void test() throws ClassNotFoundException, NoSuchMethodException, SecurityException
    {
        ApplicationContext ctx = new ApplicationContext();

        // each app could have its own sand box, but what about hosts?
        Application sandbox1 = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                // This code will be woven
            }
        });

        // TODO: what about the clock? one per box?
        // TODO: what about nodes? and scripting?

        // A box should be closed after all threads created have been terminated
        // Closing a box should terminate all threads created within it
        sandbox1.start();

        // TODO: how to link two boxes?

        Application sandbox2 = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                // TODO: how to load the byte-code?
                // would ClassLoader#getResource() work?
            }
        });

        sandbox2.start();

        sandbox1.stop();
        sandbox2.stop();
    }

    public void testRunEmptyApplication() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
            SecurityException
    {
        URL[] classPath = new URL[] { new File("target/test-classes").toURI().toURL() };
        URLClassLoader loader = new URLClassLoader(classPath, null);

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);

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
    public void test2() throws Exception
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

        eventBridge.addDownstreamEventListener(new EventListener()
        {
            @Override
            public void handleEvent(Event evt)
            {
                System.out.println("[event] " + evt);
            }
        });

        app.start();
        Thread.sleep(2000);
        app.stop().awaitUninterruptibly(5000);
    }

    public void testSeparatesLoadsCoreClassesCorrectly() throws Exception
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

    // private ClassLoader createApplicationLoader(MimicryConfiguration config) throws MalformedURLException
    // {
    // List<URL> aspects = new ArrayList<URL>();
    // aspects.addAll(config.getAspectClassPath());
    //
    // Set<URL> appClassPath;
    // appClassPath = new HashSet<URL>();
    // appClassPath.addAll(config.getAspectClassPath());
    // appClassPath.addAll(config.getBridgeClassPath());
    //
    // ClassLoader parent = TestMimicrySandbox.class.getClassLoader();
    // return new ApplicationClassLoader(appClassPath, aspects, parent);
    // }
}