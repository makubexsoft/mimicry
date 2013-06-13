package com.gc.mimicry.core.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.gc.mimicry.bridge.ApplicationBridge;
import com.gc.mimicry.bridge.LoopInterceptingByteCodeLoader;
import com.gc.mimicry.bridge.WeavingClassLoader;
import com.gc.mimicry.core.BaseResourceManager;
import com.gc.mimicry.core.ChildFirstURLClassLoader;
import com.gc.mimicry.core.ClassLoadingContext;
import com.gc.mimicry.core.deployment.ApplicationDescriptor;
import com.gc.mimicry.util.ClassPathUtil;
import com.google.common.base.Preconditions;

public class ApplicationManager extends BaseResourceManager
{
	public ApplicationManager(ClassLoadingContext context, Node node)
	{
		Preconditions.checkNotNull( context );
		Preconditions.checkNotNull( node );

		this.context = context;
		this.node = node;

		applications = new HashSet<Application>();
	}

	public Set<Application> getApplications()
	{
		return applications;
	}

	public Application getApplication( UUID id )
	{
		for ( Application app : applications )
		{
			if ( app.getId().equals( id ) )
			{
				return app;
			}
		}
		return null;
	}

	// TODO: remove hard coded paths
	public Application launchApplication( ApplicationDescriptor appDesc ) throws IOException
	{
		ChildFirstURLClassLoader outerClassLoader;
		outerClassLoader = new ChildFirstURLClassLoader( new URL[]
		{ new File( "../mimicry-bridge/target/classes" ).toURI().toURL() }, context.getCoreClassLoader() );

		List<URL> aspectUrls = new ArrayList<URL>();
		aspectUrls.addAll( context.getAspectClassPath() );

		Set<String> referencedClassPath = new HashSet<String>( appDesc.getClassPath() );
		referencedClassPath.add( "./target/classes" );

		Set<URL> aspectJClassPath;
		aspectJClassPath = new HashSet<URL>( Arrays.asList( ClassPathUtil.createClassPath( appDesc.getClassPath() ) ) );
		aspectJClassPath.addAll( context.getAspectClassPath() );
		aspectJClassPath.addAll( context.getBridgeClassPath() );

		LoopInterceptingByteCodeLoader codeLoader;
		codeLoader = new LoopInterceptingByteCodeLoader( referencedClassPath.toArray( new String[0] ) );
		WeavingClassLoader loader = new WeavingClassLoader( aspectJClassPath, aspectUrls, codeLoader, outerClassLoader );

		ApplicationBridge bridge = new ApplicationBridge( loader );
		bridge.setMainClass( appDesc.getMainClass() );
		bridge.setCommandArgs( appDesc.getCommandLine() );

		Application app = new Application( node, bridge );

		applications.add( app );
		attachResource( app );

		return app;
	}

	private final Set<Application>		applications;
	private final Node					node;
	private final ClassLoadingContext	context;
}
