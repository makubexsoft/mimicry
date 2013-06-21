package com.gc.mimicry.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipFileExtractor implements Closeable
{

	public static final String	EVERTHING_PATTERN	= "";
	private ZipFile				zipFile;

	public ZipFileExtractor(File file) throws ZipException, IOException
	{
		zipFile = new ZipFile( file );
	}

	public void close()
	{
		IOUtils.closeSilently( new Closeable()
		{
			public void close() throws IOException
			{
				zipFile.close();
			}
		} );
	}

	public Set<String> extractAll( File destinationDir ) throws IOException
	{
		return extract( EVERTHING_PATTERN, destinationDir );
	}

	public Set<String> extract( String srcPath, File destinationDir ) throws IOException
	{
		Set<String> extractedFiles = new HashSet<String>();

		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while ( entries.hasMoreElements() )
		{
			ZipEntry entry = entries.nextElement();

			if ( entry.getName().equalsIgnoreCase( srcPath ) || entry.getName().startsWith( addSlash( srcPath ) ) )
			{
				createDirs( destinationDir, entry.getName() );
				if ( !entry.isDirectory() )
				{
					IOUtils.writeToFile( zipFile.getInputStream( entry ), new File( destinationDir, entry.getName() ) );
				}
				extractedFiles.add( entry.getName() );
			}
		}

		return extractedFiles;
	}

	private void createDirs( File baseDir, String name )
	{
		int index = name.lastIndexOf( "/" );
		if ( index > 0 )
		{
			File dir = new File( baseDir, name.substring( 0, index ) );
			dir.mkdirs();
		}
	}

	private String addSlash( String s )
	{
		if ( s.endsWith( "/" ) )
		{
			return s;
		}
		if ( s.equals( "" ) )
		{
			return "";
		}
		return s + "/";
	}
}
