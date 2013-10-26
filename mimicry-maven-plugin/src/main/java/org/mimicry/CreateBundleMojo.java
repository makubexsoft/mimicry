package org.mimicry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mimicry.engine.deployment.BundleCreator;
import org.mimicry.engine.deployment.LocalApplicationRepository;
import org.mimicry.util.AntPathMatcher;
import org.slf4j.impl.StaticLoggerBinder;


/**
 * Creates an application bundle for the Mimicry Framework.
 * 
 * @goal createBundle
 * @phase package
 * @author Marc-Christian Schulze
 */
@Mojo(name = "createBundle", defaultPhase = LifecyclePhase.PACKAGE)
public class CreateBundleMojo extends AbstractMojo
{
	/**
	 * The name of the bundle to create.
	 * 
	 * @parameter expression="${project.name}-${project.version}", required=true
	 */
	@Parameter(defaultValue = "${project.name}-${project.version}", required = true)
	private String			bundleName;

	/**
	 * Fully qualified class name of the main class.
	 * 
	 * @parameter required=true
	 */
	@Parameter(required = true)
	private String			mainClassFile;

	/**
	 * The file sets to copy into the bundle.
	 * 
	 * @parameter required=true
	 */
	@Parameter(required = true)
	private List<FileSet>	content;

	/**
	 * The class path entries.
	 * 
	 * @parameter required=true
	 */
	@Parameter(required = true)
	private List<String>	classPath;

	/**
	 * List of supported operating systems.
	 * 
	 * @parameter
	 */
	@Parameter
	private List<String>	supportedOS	= new ArrayList<String>();

	/**
	 * The directory in which the bundle file will be written.
	 * 
	 * @parameter expression="${project.build.directory}", required=true
	 */
	@Parameter(required = true, defaultValue = "${project.build.directory}")
	private File			targetDirectory;

	/**
	 * Whether the bundle should be deployed to the local Mimicry repository.
	 * 
	 * @parameter
	 */
	@Parameter
	private boolean			deployToLocalRepository;

	private BundleCreator	bundleCreator;
	private File			bundle;
	private AntPathMatcher	matcher		= new AntPathMatcher();

	/**
	 * The entry point of the maven plugin.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		StaticLoggerBinder.getSingleton().setMavenLog(getLog());
		
		getLog().info( "Creating bundle '" + bundleName + "'..." );
		bundleCreator = new BundleCreator( bundleName );
		bundleCreator.setMainClass( mainClassFile );
		for ( String cp : classPath )
		{
			bundleCreator.addClassPathEntry( cp );
		}
		for ( String osName : supportedOS )
		{
			bundleCreator.addSupportedOperatingSystem( osName );
		}

		getLog().info( "Writing bundle to " + targetDirectory );

		addContentToBundle();

		writeBundle();

		deployBundleIfConfigured();
	}

	private void addContentToBundle()
	{
		for ( FileSet fileSet : content )
		{
			if ( fileSet.getIncludes().size() == 0 && fileSet.getExcludes().size() == 0 )
			{
				addFile( fileSet.getSource(), fileSet.getTarget() );
			}
			else
			{
				addDirectoryContentConsideringPatterns( fileSet );
			}
		}
	}

	private void deployBundleIfConfigured() throws MojoExecutionException
	{
		if ( deployToLocalRepository )
		{
			deployBundle();
		}
	}

	private void deployBundle() throws MojoExecutionException
	{
		getLog().info( "Deploying bundle to local repository " + LocalApplicationRepository.getDefaultPath() );
		try
		{
			LocalApplicationRepository appRepo = new LocalApplicationRepository();
			BufferedInputStream in = new BufferedInputStream( new FileInputStream( bundle ) );
			appRepo.storeBundle( bundleName, in );
		}
		catch ( IOException e )
		{
			throw new MojoExecutionException( "Failed to deploy bundle " + bundleName + " to local repository at "
					+ LocalApplicationRepository.getDefaultPath() );
		}
	}

	private void writeBundle() throws MojoExecutionException
	{
		try
		{
			bundle = bundleCreator.createBundle( targetDirectory );
			getLog().info( "Bundle has been written to file " + bundle );
		}
		catch ( IOException e )

		{
			getLog().error( "Failed to write bundle to " + targetDirectory, e );
			throw new MojoExecutionException( "Failed to write bundle to " + targetDirectory, e );
		}
	}

	private void addDirectoryContentConsideringPatterns( FileSet fileSet )
	{
		if ( !fileSet.getSource().isDirectory() )
		{
			getLog().warn( "Ignoring includes and excludes for simple file: " + fileSet.getSource() );
			addFile( fileSet.getSource(), fileSet.getTarget() );
			return;
		}

		File[] subFiles = fileSet.getSource().listFiles();
		for ( File file : subFiles )
		{
			addIfMatching( fileSet.getSource(), file, fileSet.getTarget() + File.separator + file.getName(),
					fileSet.getIncludes(), fileSet.getExcludes() );
		}
	}

	private void addIfMatching( File baseDirectory, File file, String path, List<String> includes, List<String> excludes )
	{
		if ( file.isDirectory() )
		{
			File[] subFiles = file.listFiles();
			for ( File f : subFiles )
			{
				addIfMatching( baseDirectory, f, path + File.separator + f.getName(), includes, excludes );
			}
		}
		else
		{
			addFileIfMatching( baseDirectory, file, path, includes, excludes );
		}
	}

	private void addFileIfMatching( File baseDirectory, File file, String path, List<String> includes,
			List<String> excludes )
	{
		String relativePath = getSubPath( baseDirectory, file );
		for ( String pattern : includes )
		{
			if ( !matcher.matches( pattern, relativePath ) )
			{
				return;
			}
		}

		for ( String pattern : excludes )
		{
			if ( matcher.matches( pattern, relativePath ) )
			{
				return;
			}
		}

		addFile( file, path );
	}

	private String getSubPath( File baseDirectory, File fullPath )
	{
		String subPath = fullPath.toString().substring( baseDirectory.toString().length() );
		if ( subPath.startsWith( File.separator ) )
		{
			subPath = subPath.substring( 1 );
		}
		return subPath;
	}

	private void addFile( File file, String path )
	{
		if ( getLog().isDebugEnabled() )
		{
			getLog().debug( "Adding file: " + file + " -> " + path );
		}
		bundleCreator.addFile( file, path );
	}
}