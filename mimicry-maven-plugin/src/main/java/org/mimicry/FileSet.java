package org.mimicry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of files that shall be copied into the bundle.
 * 
 * @author Marc-Christian Schulze
 */
public class FileSet
{
	private File			source;
	private String			target;
	private List<String>	includes;
	private List<String>	excludes;

	public FileSet()
	{
		includes = new ArrayList<String>();
		excludes = new ArrayList<String>();
	}

	public FileSet(File source, String target)
	{
		this.source = source;
		this.target = target;
		includes = new ArrayList<String>();
		excludes = new ArrayList<String>();
	}

	/**
	 * Returns the source file which should be copied. If the returned file
	 * represents a directory the includes and excludes are applied for
	 * filtering.
	 * 
	 * @return
	 */
	public File getSource()
	{
		return source;
	}

	public void setSource( File source )
	{
		this.source = source;
	}

	/**
	 * Returns a relative target path under which the copied files should be
	 * stored within the bundle.
	 * 
	 * @return
	 */
	public String getTarget()
	{
		return target;
	}

	public void setTarget( String target )
	{
		this.target = target;
	}

	/**
	 * Returns a list of ant-style include patterns that iff specified must be
	 * matched. The patterns are applied to the file's path relative to the
	 * source directory.
	 * 
	 * @return
	 */
	public List<String> getIncludes()
	{
		return includes;
	}

	public void setIncludes( List<String> includes )
	{
		this.includes = includes;
	}

	/**
	 * Returns a list of ant-style include patterns that iff specified must NOT
	 * be matched. The patterns are applied to the file's path relative to the
	 * source directory.
	 * 
	 * @return
	 */
	public List<String> getExcludes()
	{
		return excludes;
	}

	public void setExcludes( List<String> excludes )
	{
		this.excludes = excludes;
	}
}
