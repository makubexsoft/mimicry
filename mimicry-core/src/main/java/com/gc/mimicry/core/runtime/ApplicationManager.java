package com.gc.mimicry.core.runtime;

import java.io.IOException;
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
import com.gc.mimicry.bridge.LoopInterceptingByteCodeLoader;
import com.gc.mimicry.bridge.WeavingClassLoader;
import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.ChildFirstURLClassLoader;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.core.event.Node;
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
    public Application launchApplication(ApplicationDescriptor appDesc) throws IOException
    {
        ChildFirstURLClassLoader outerClassLoader;
        outerClassLoader = new ChildFirstURLClassLoader(context.getBridgeClassPath(), Thread.currentThread()
                .getContextClassLoader());

        List<URL> aspectUrls = new ArrayList<URL>();
        aspectUrls.addAll(context.getAspectClassPath());

        Set<URL> aspectJClassPath;
        aspectJClassPath = new HashSet<URL>(Arrays.asList(ClassPathUtil.createClassPath(appDesc.getClassPath())));
        aspectJClassPath.addAll(context.getAspectClassPath());
        aspectJClassPath.addAll(context.getBridgeClassPath());

        LoopInterceptingByteCodeLoader codeLoader = createApplicationClassLoader(appDesc);
        WeavingClassLoader loader = new WeavingClassLoader(aspectJClassPath, aspectUrls, codeLoader, outerClassLoader);

        ApplicationBridge bridge = new ApplicationBridge(loader);
        bridge.setMainClass(appDesc.getMainClass());
        bridge.setCommandArgs(appDesc.getCommandLine());
        bridge.setEventBridge(node.getEventBridge());
        bridge.setClock(node.getClock());

        Application app = new Application(node, bridge);

        applications.add(app);
        attachResource(app);

        return app;
    }

    private LoopInterceptingByteCodeLoader createApplicationClassLoader(ApplicationDescriptor appDesc)
    {
        //
        // In order to resolve all symbols in soot
        // we need to setup the classpath which can be referenced by soot
        // App.'s Classpath + Core Bundle
        //
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
