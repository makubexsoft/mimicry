package com.gc.mimicry.engine.local;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.ApplicationContext;
import com.gc.mimicry.engine.ClassPathConfiguration;
import com.gc.mimicry.engine.EngineInfo;
import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.Node;
import com.gc.mimicry.engine.deployment.ApplicationBundle;
import com.gc.mimicry.engine.deployment.ApplicationRepository;
import com.gc.mimicry.engine.stack.EventBridge;
import com.gc.mimicry.engine.stack.EventStack;
import com.gc.mimicry.engine.timing.Timeline;
import com.gc.mimicry.util.BaseResourceManager;
import com.gc.mimicry.util.IOUtils;
import com.gc.mimicry.util.ZipFileExtractor;
import com.google.common.base.Preconditions;

/**
 * A node represents a logical machine on which simulated applications can be run. An instance of a node only exists
 * within a certain simulation session.
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

    public LocalNode(String name, EventEngine eventBroker, Timeline timeline, ApplicationRepository appRepo,
            File fileSystemRoot)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(appRepo);
        Preconditions.checkNotNull(fileSystemRoot);

        this.name = name;
        this.timeline = timeline;
        this.appRepo = appRepo;
        this.fileSystemRoot = fileSystemRoot;

        applications = new HashSet<LocalApplication>();
        eventBridge = new EventBridge();
        eventStack = new EventStack(this, eventBroker, eventBridge);
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

            ClassPathConfiguration config = ClassPathConfiguration.deriveFromClassPath();
            for (String classPath : bundle.getClassPath())
            {
                URL url = new File(installDir, classPath).toURI().toURL();
                config.addAppClassPath(url);
            }

            return createApplication(bundle.getMainClass(), config);
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
        ClassLoader loader = ApplicationClassLoader.create(config, ClassLoader.getSystemClassLoader());
        ApplicationContext ctx = new ApplicationContext();
        ctx.setClassLoader(loader);
        ctx.setClock(getTimeline());
        ctx.setEventBridge(getEventBridge());

        LocalApplication application = Applications.create(ctx, mainClass);

        applications.add(application);
        attachResource(application);
        return application;
    }

    @Override
    public EngineInfo getEngineInfo()
    {
        return EngineInfo.fromLocalJVM();
    }
}
