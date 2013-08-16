package com.gc.mimicry.engine.apps;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.weaving.ApplicationClassLoader;
import com.gc.mimicry.engine.MimicryConfiguration;
import com.gc.mimicry.engine.deployment.ApplicationBundleDescriptor;
import com.gc.mimicry.engine.nodes.Node;
import com.gc.mimicry.util.BaseResourceManager;
import com.google.common.base.Preconditions;

/**
 * The application manager is part of a {@link Node} and is responsible for loading new applications and managing their
 * references.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationManager extends BaseResourceManager
{
    public ApplicationManager(MimicryConfiguration context, Node node)
    {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(node);

        this.context = context;
        this.node = node;

        applications = new HashSet<Application>();
    }

    /**
     * Returns references to all launched applications.
     * 
     * @return
     */
    public Set<Application> getApplications()
    {
        return applications;
    }

    /**
     * Returns the application identified by the given id if it is managed by this instance; otherwise null.
     * 
     * @param id
     * @return
     */
    public Application getApplication(UUID id)
    {
        for (Application app : applications)
        {
            if (app.getId().equals(id))
            {
                return app;
            }
        }
        return null;
    }

    /**
     * Launches a new application instance. This might take a moment.
     * 
     * @param appDesc
     * @return
     * @throws IOException
     */
    public Application launchApplication(ApplicationBundleDescriptor appDesc) throws IOException
    {
        ApplicationClassLoader loader = ApplicationClassLoader.create(null);// TODO:appDesc);

        ApplicationBridge bridge = createBridge(appDesc, loader);

        return createApplication(bridge);
    }

    private Application createApplication(ApplicationBridge bridge)
    {
        Application app = new Application(node, bridge);
        applications.add(app);
        attachResource(app);
        return app;
    }

    private ApplicationBridge createBridge(ApplicationBundleDescriptor appDesc, ApplicationClassLoader loader)
    {
        ApplicationBridge bridge = new ApplicationBridge(loader);
        bridge.setEventBridge(node.getEventBridge());
        bridge.setClock(node.getClock());
        return bridge;
    }

    // private ApplicationClassLoader createClassLoader(ApplicationBundleDescriptor appDesc) throws
    // MalformedURLException
    // {
    // ClassLoader parentCL = Thread.currentThread().getContextClassLoader();
    // MeFirstClassLoader outerClassLoader;
    // outerClassLoader = new MeFirstClassLoader(context.getBridgeClassPath(), parentCL);
    //
    // List<URL> aspectUrls = new ArrayList<URL>();
    // aspectUrls.addAll(context.getAspectClassPath());
    //
    // Set<URL> aspectJClassPath;
    // aspectJClassPath = new HashSet<URL>();
    // aspectJClassPath.addAll(context.getAspectClassPath());
    // aspectJClassPath.addAll(context.getBridgeClassPath());
    //
    // ApplicationClassLoader loader = new ApplicationClassLoader(aspectJClassPath, aspectUrls, outerClassLoader);
    // return loader;
    // }

    private final Set<Application> applications;
    private final Node node;
    private final MimicryConfiguration context;
}
