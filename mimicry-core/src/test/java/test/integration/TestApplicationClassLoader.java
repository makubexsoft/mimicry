package test.integration;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.siddhi.SiddhiCEPEngine;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.local.Applications;
import com.gc.mimicry.engine.local.LocalApplication;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.SystemClock;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.util.concurrent.Future;

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