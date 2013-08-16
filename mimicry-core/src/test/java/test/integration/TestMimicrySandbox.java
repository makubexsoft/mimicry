package test.integration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.Application;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.Applications;
import com.gc.mimicry.engine.MimicryConfiguration;
import com.gc.mimicry.engine.EntryPoint;
import com.gc.mimicry.engine.Event;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.timing.Clock;
import com.gc.mimicry.engine.timing.SystemClock;
import com.gc.mimicry.util.FileNameExtensionFilter;
import com.gc.mimicry.util.IOUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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
    public void test2() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            InterruptedException
    {
        // Read configuration
        File bridgeDir = new File("../mimicry-bridge/target");
        File aspectDir = new File("../mimicry-aspects/target");
        File coreDir = new File("target");
        File pluginDir = new File("../mimicry-plugin-core/target");

        // Collect jar files in plugin folders
        List<File> bridgeJarFiles = IOUtils.collectFiles(bridgeDir, new FileNameExtensionFilter(
                "bridge-0.0.1-SNAPSHOT.jar"));
        List<File> aspectJarFiles = IOUtils.collectFiles(aspectDir, new FileNameExtensionFilter(
                "aspects-0.0.1-SNAPSHOT.jar"));
        List<File> coreJarFiles = IOUtils.collectFiles(coreDir, new FileNameExtensionFilter("core-0.0.1-SNAPSHOT.jar"));
        List<File> pluginJarFiles = IOUtils.collectFiles(pluginDir, new FileNameExtensionFilter(
                "plugin-core-0.0.1-SNAPSHOT.jar"));

        // Create class loader for event handler
        URLClassLoader eventHandlerCL = createEHClassLoader(coreJarFiles, pluginJarFiles);

        // Create context
        MimicryConfiguration clctx = createContext(bridgeJarFiles, aspectJarFiles, eventHandlerCL);

        URLClassLoader loader = createApplicationLoader(clctx);

        //
        // -------------------------------------------
        //
        EventBridge eventBridge = new EventBridge();

        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(new SystemClock());
        ctx.setEventBridge(eventBridge);

        Class<?> class1 = ctx.getClassLoader().loadClass(Clock.class.getName());
        assertEquals(System.identityHashCode(Clock.class), System.identityHashCode(class1));

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
                System.out.println("Application send event: " + evt);
            }
        });

        System.out.println("Starting...");
        app.start();
        System.out.println("Running");
        Thread.sleep(2000);
        app.stop().awaitUninterruptibly(5000);
        System.out.println("end.");
    }

    private static URLClassLoader createEHClassLoader(List<File> coreJarFiles, List<File> pluginJarFiles)
    {
        List<File> tmp = new ArrayList<File>();
        tmp.addAll(coreJarFiles);
        tmp.addAll(pluginJarFiles);
        Collection<URL> urls = Collections2.transform(tmp, new Function<File, URL>()
        {
            @Override
            public URL apply(File f)
            {
                try
                {
                    return f.toURI().toURL();
                }
                catch (MalformedURLException e)
                {
                    return null;
                }
            }
        });
        URLClassLoader eventHandlerCL = new URLClassLoader(urls.toArray(new URL[0]),
                TestMimicrySandbox.class.getClassLoader());
        return eventHandlerCL;
    }

    private static MimicryConfiguration createContext(List<File> bridgeJarFiles, List<File> aspectJarFiles,
            URLClassLoader eventHandlerCL) throws MalformedURLException
    {
        MimicryConfiguration ctx;
        ctx = new MimicryConfiguration(eventHandlerCL);
        for (File jarFile : aspectJarFiles)
        {
            ctx.addAspectClassPath(jarFile.toURI().toURL());
            ctx.addBridgeClassPath(jarFile.toURI().toURL());
        }
        for (File jarFile : bridgeJarFiles)
        {
            ctx.addBridgeClassPath(jarFile.toURI().toURL());
        }
        return ctx;
    }

    private ApplicationClassLoader createApplicationLoader(MimicryConfiguration config) throws MalformedURLException
    {
        List<URL> aspects = new ArrayList<URL>();
        aspects.addAll(config.getAspectClassPath());

        Set<URL> appClassPath;
        appClassPath = new HashSet<URL>();
        appClassPath.addAll(config.getAspectClassPath());
        appClassPath.addAll(config.getBridgeClassPath());

        ClassLoader parent = TestMimicrySandbox.class.getClassLoader();
        return new ApplicationClassLoader(appClassPath, aspects, parent);
    }
}