package com.gc.mimicry.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class ClassPathUtil
{
	public static URL[] createClassPath( Collection<String> paths )
	{
		URL[] urls = new URL[paths.size()];
		int i = 0;
		for ( String u : paths )
		{
			try
			{
				urls[i++] = new File( u ).toURI().toURL();
			}
			catch ( MalformedURLException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return urls;
	}

	public static URL[] createClassPath( String... paths )
	{
		URL[] urls = new URL[paths.length];
		int i = 0;
		for ( String u : paths )
		{
			try
			{
				urls[i++] = new File( u ).toURI().toURL();
			}
			catch ( MalformedURLException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return urls;
	}
}
