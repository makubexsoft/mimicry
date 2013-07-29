package com.gc.mimicry.engine.apps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.weaving.LoopInterceptingByteCodeLoader;
import com.gc.mimicry.bridge.weaving.WeavingClassLoader;
import com.gc.mimicry.engine.ClassLoadingContext;
import com.gc.mimicry.engine.deployment.ApplicationBundleDescriptor;
import com.gc.mimicry.engine.nodes.Node;
import com.gc.mimicry.util.BaseResourceManager;
import com.gc.mimicry.util.ChildFirstURLClassLoader;
import com.gc.mimicry.util.ClassPathUtil;
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
    public ApplicationManager(ClassLoadingContext context, Node node)
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
        WeavingClassLoader loader = createClassLoader(appDesc);

        ApplicationBridge bridge = createBridge(appDesc, loader);

        return createApplication(bridge);
    }

    private ApplicationBridge createBridge(ApplicationBundleDescriptor appDesc, WeavingClassLoader loader)
    {
        ApplicationBridge bridge = new ApplicationBridge(loader);
        bridge.setMainClass(appDesc.getMainClass());
        bridge.setEventBridge(node.getEventBridge());
        bridge.setClock(node.getClock());
        return bridge;
    }

    private WeavingClassLoader createClassLoader(ApplicationBundleDescriptor appDesc) throws MalformedURLException
    {
        ClassLoader parentCL = Thread.currentThread().getContextClassLoader();
        ChildFirstURLClassLoader outerClassLoader;
        outerClassLoader = new ChildFirstURLClassLoader(context.getBridgeClassPath(), parentCL);

        List<URL> aspectUrls = new ArrayList<URL>();
        aspectUrls.addAll(context.getAspectClassPath());

        Set<URL> aspectJClassPath;
        aspectJClassPath = new HashSet<URL>(Arrays.asList(ClassPathUtil.createClassPath(appDesc.getClassPath())));
        aspectJClassPath.addAll(context.getAspectClassPath());
        aspectJClassPath.addAll(context.getBridgeClassPath());

        LoopInterceptingByteCodeLoader codeLoader = createApplicationClassLoader(appDesc);
        WeavingClassLoader loader = new WeavingClassLoader(aspectJClassPath, aspectUrls, codeLoader, outerClassLoader);
        return loader;
    }

    private Application createApplication(ApplicationBridge bridge)
    {
        Application app = new Application(node, bridge);
        applications.add(app);
        attachResource(app);
        return app;
    }

    private LoopInterceptingByteCodeLoader createApplicationClassLoader(ApplicationBundleDescriptor appDesc)
    {
        Set<String> referencedClassPath = new HashSet<String>(appDesc.getClassPath());
        referencedClassPath.addAll(ClassPathUtil.getSystemClassPath());
        LoopInterceptingByteCodeLoader codeLoader;
        codeLoader = new LoopInterceptingByteCodeLoader(referencedClassPath.toArray(new String[0]));
        return codeLoader;
    }

    private static final Logger logger;
    static
    {
        logger = LoggerFactory.getLogger(ApplicationManager.class);
    }
    private final Set<Application> applications;
    private final Node node;
    private final ClassLoadingContext context;
}
