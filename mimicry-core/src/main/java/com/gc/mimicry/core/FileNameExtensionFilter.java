package com.gc.mimicry.core;

import java.io.File;

public class FileNameExtensionFilter implements java.io.FilenameFilter
{
	public FileNameExtensionFilter(String extension)
	{
		this.extension = extension;
	}

	@Override
	public boolean accept( File dir, String name )
	{
		return name.toLowerCase().endsWith( extension.toLowerCase() );
	}

	private final String	extension;
}
