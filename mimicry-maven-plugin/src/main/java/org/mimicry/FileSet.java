package org.mimicry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSet
{
	private File	source;
	private String	target;
	private List<String> includes;
	private List<String> excludes;

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

	public File getSource()
	{
		return source;
	}

	public void setSource( File source )
	{
		this.source = source;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget( String target )
	{
		this.target = target;
	}

	public List<String> getIncludes()
	{
		return includes;
	}

	public void setIncludes( List<String> includes )
	{
		this.includes = includes;
	}

	public List<String> getExcludes()
	{
		return excludes;
	}

	public void setExcludes( List<String> excludes )
	{
		this.excludes = excludes;
	}
}
