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

import com.gc.mimicry.engine.deployment.BundleCreator;
import com.gc.mimicry.engine.deployment.LocalApplicationRepository;
import com.gc.mimicry.util.AntPathMatcher;

/**
 * Creates an application bundle for the Mimicry Framework.
 * 
 * @goal createBundle
 * @phase package
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
	 * 
	 * @parameter
	 */
	@Parameter
	private boolean			deployToLocalRepository;

	private BundleCreator	bundleCreator;

	private AntPathMatcher	matcher		= new AntPathMatcher();

	public void execute() throws MojoExecutionException, MojoFailureException
	{
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

		File bundle;
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

		if ( deployToLocalRepository )
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
			addIfMatching( file, fileSet.getTarget() + File.separator + file.getName(), fileSet.getIncludes(),
					fileSet.getExcludes() );
		}
	}

	private void addIfMatching( File file, String path, List<String> includes, List<String> excludes )
	{
		if ( file.isDirectory() )
		{
			File[] subFiles = file.listFiles();
			for ( File f : subFiles )
			{
				addIfMatching( f, path + File.separator + f.getName(), includes, excludes );
			}
		}
		else
		{
			addFileIfMatching( file, path + File.separator + file.getName(), includes, excludes );
		}
	}

	private void addFileIfMatching( File file, String path, List<String> includes, List<String> excludes )
	{
		for ( String pattern : includes )
		{
			if ( !matcher.matches( pattern, file.toString() ) )
			{
				return;
			}
		}

		for ( String pattern : excludes )
		{
			if ( matcher.matches( pattern, file.toString() ) )
			{
				return;
			}
		}

		addFile( file, path );
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