package com.gc.mimicry.engine.local;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.bridge.EntryPoint;
import com.gc.mimicry.bridge.threading.CheckpointBasedScheduler;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.Node;
import com.gc.mimicry.engine.deployment.ApplicationBundle;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.stack.EventStack;
import com.gc.mimicry.engine.streams.ApplicationHasBeenInstalledStream;
import com.gc.mimicry.engine.streams.NodeRemovedStream;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.util.BaseResourceManager;
import com.gc.mimicry.util.IOUtils;
import com.gc.mimicry.util.ZipFileExtractor;
import com.google.common.base.Preconditions;

/**
 * A simulated node that is hosted on a {@link LocalSession} within the local JVM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class LocalNode extends BaseResourceManager implements Node
{
    private final UUID id;
    private final String name;
    private final EventStack eventStack;
    private final EventBridge eventBridge;
    private final Set<LocalApplication> applications;
    private final Timeline timeline;
    private final ApplicationRepository appRepo;
    private final File fileSystemRoot;
    private final CEPEngine eventEngine;

    public LocalNode(String name, CEPEngine eventEngine, Timeline timeline, ApplicationRepository appRepo,
            File fileSystemRoot)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(eventEngine);
        Preconditions.checkNotNull(appRepo);
        Preconditions.checkNotNull(fileSystemRoot);

        this.name = name;
        this.timeline = timeline;
        this.appRepo = appRepo;
        this.fileSystemRoot = fileSystemRoot;
        this.eventEngine = eventEngine;

        applications = new HashSet<LocalApplication>();
        eventBridge = new EventBridge(eventEngine);
        eventStack = new EventStack(this, eventBridge);
        id = UUID.randomUUID();
    }

    public File getFileSystemRoot()
    {
        return fileSystemRoot;
    }

    public Timeline getTimeline()
    {
        return timeline;
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    public EventBridge getEventBridge()
    {
        return eventBridge;
    }

    public EventStack getEventStack()
    {
        return eventStack;
    }

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Returns references to all launched applications.
     * 
     * @return
     */
    public Set<LocalApplication> getApplications()
    {
        return applications;
    }

    @Override
    public void close()
    {
        NodeRemovedStream.get(eventEngine).send(timeline.currentMillis(), getId());
        super.close();
    }

    /**
     * Returns the application identified by the given id if it is managed by this instance; otherwise null.
     * 
     * @param id
     * @return
     */
    public LocalApplication getApplication(UUID id)
    {
        for (LocalApplication app : applications)
        {
            if (app.getId().equals(id))
            {
                return app;
            }
        }
        return null;
    }

    @Override
    public LocalApplication installApplication(String bundleName, String path)
    {
        ApplicationBundle bundle = appRepo.findBundle(bundleName);

        File installDir = new File(getFileSystemRoot(), path);
        installDir.mkdirs();
        ZipFileExtractor extractor = null;
        try
        {
            extractor = new ZipFileExtractor(bundle.getLocalLocation());
            extractor.extractAll(installDir);

            ClassPathConfiguration config = ClassPathConfiguration.deriveFromSystemClassLoader();
            for (String classPath : bundle.getClassPath())
            {
                URL url = new File(installDir, classPath).toURI().toURL();
                config.addToStage2ClassPath(url);
            }

            LocalApplication application = createApplication(bundle.getMainClass(), config);

            ApplicationHasBeenInstalledStream.get(eventEngine).send(timeline.currentMillis(), getId(),
                    application.getId(), bundleName, path);

            return application;
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException("Failed to load application due to invalid class path.", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load application.", e);
        }
        finally
        {
            IOUtils.closeSilently(extractor);
        }
    }

    private LocalApplication createApplication(String mainClass, ClassPathConfiguration config)
            throws MalformedURLException, NoSuchMethodException, SecurityException, ClassNotFoundException
    {
        ClassLoader loader = ApplicationClassLoader.create(config);
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(getTimeline());
        ctx.setEventBridge(getEventBridge());

        LocalApplication application = create(ctx, mainClass);

        applications.add(application);
        attachResource(application);
        return application;
    }

    @Override
    public EngineInfo getEngineInfo()
    {
        return EngineInfo.fromLocalJVM();
    }

    private LocalApplication create(final ApplicationContext ctx, String mainClassName) throws NoSuchMethodException,
            SecurityException, ClassNotFoundException
    {
        Class<?> mainClass = ctx.getClassLoader().loadClass(mainClassName);
        final Method mainMethod = mainClass.getMethod("main", String[].class);

        Class<?> threadClass = ctx.getClassLoader().loadClass("com.gc.mimicry.bridge.threading.ManagedThread");
        final Constructor<?> constructor = threadClass.getConstructor(Runnable.class);

        EntryPoint r = new EntryPoint()
        {
            @Override
            public void main(final String[] args) throws Throwable
            {
                Thread thread = (Thread) constructor.newInstance(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            mainMethod.invoke(null, new Object[] { args });
                        }
                        catch (Throwable e)
                        {
                            throw new RuntimeException("Thread terminated due to uncaught exception.", e);
                        }
                    }
                });
                thread.setContextClassLoader(ctx.getClassLoader());
                thread.start();
            }
        };
        return new LocalApplication(ctx, r, new CheckpointBasedScheduler(ctx.getTimeline()), eventEngine);
    }
}
