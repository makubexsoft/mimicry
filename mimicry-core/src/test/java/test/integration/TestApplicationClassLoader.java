package test.integration;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.mimicry.bridge.EntryPoint;
import org.mimicry.bridge.weaving.ApplicationClassLoader;
import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.siddhi.SiddhiCEPEngine;
import org.mimicry.engine.ApplicationContext;
import org.mimicry.engine.ClassPathConfiguration;
import org.mimicry.engine.local.Applications;
import org.mimicry.engine.local.LocalApplication;
import org.mimicry.engine.stack.EventBridge;
import org.mimicry.engine.timing.SystemClock;
import org.mimicry.engine.timing.Timeline;
import org.mimicry.util.concurrent.Future;


public class TestApplicationClassLoader
{
    private ApplicationContext ctx;
    private CEPEngine eventEngine;

    @Before
    public void setUp() throws Exception
    {
        eventEngine = new SiddhiCEPEngine();

        ClassPathConfiguration classpath = createConfig();
        ApplicationClassLoader loader = ApplicationClassLoader.create(classpath);

        ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(new EventBridge(eventEngine));
    }

    @Test
    public void testRunEmptyApplication() throws Exception
    {
        LocalApplication app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
            }
        }, eventEngine);
        app.start();
    }

    @Test
    public void testCanStopInifiteLoop() throws Exception
    {
        LocalApplication app = Applications.create(ctx, new EntryPoint()
        {
            @Override
            public void main(String[] args)
            {
                for (;;)
                {
                    // avoid optimization
                    System.out.print("");
                }
            }
        }, eventEngine);

        app.start();
        Future<?> future = app.stop();
        future.awaitUninterruptibly(1000);

        assertTrue(future.isSuccess());
    }

    @Test
    public void testLoadsCoreClassesAtStage2() throws Exception
    {
        Class<?> loadedClass = ctx.getClassLoader().loadClass(Timeline.class.getName());

        assertTrue(Timeline.class == loadedClass);
    }

    //
    // Infrastructure
    //
    private ClassPathConfiguration createConfig() throws MalformedURLException
    {
        File bridgeJar = new File("../mimicry-bridge/target/classes");
        File aspectJar = new File("../mimicry-aspects/target/classes");

        ClassPathConfiguration ctx = ClassPathConfiguration.createEmpty();
        ctx.addAspectClassPath(aspectJar.toURI().toURL());
        ctx.addToStage1ClassPath(bridgeJar.toURI().toURL());

        return ctx;
    }
}