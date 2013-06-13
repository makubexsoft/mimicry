package com.gc.mimicry.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import stubs.ExampleClass;

public class TestChildFirstURLClassLoader
{

	@Test
	public void testParentRecovers() throws ClassNotFoundException, IOException
	{
		URLClassLoader parentLoader = new URLClassLoader( createURLs( "target/test-classes" ) );
		ChildFirstURLClassLoader childLoader1 = new ChildFirstURLClassLoader( createURLs(), parentLoader );
		ChildFirstURLClassLoader childLoader2 = new ChildFirstURLClassLoader( createURLs(), parentLoader );

		Class<?> class1 = childLoader1.loadClass( "stubs.ExampleClass" );
		Class<?> class2 = childLoader2.loadClass( "stubs.ExampleClass" );

		assertNotNull( class1 );
		assertNotNull( class2 );
		assertEquals( class1, class2 );

		childLoader1.close();
		childLoader2.close();
		parentLoader.close();
	}

	@Test
	public void testChildLoadsFirst() throws ClassNotFoundException, IOException
	{
		URLClassLoader parentLoader = new URLClassLoader( createURLs( "target/test-classes" ) );
		ChildFirstURLClassLoader childLoader1 = new ChildFirstURLClassLoader( createURLs( "target/test-classes" ),
				parentLoader );
		ChildFirstURLClassLoader childLoader2 = new ChildFirstURLClassLoader( createURLs( "target/test-classes" ),
				parentLoader );

		Class<?> class1 = childLoader1.loadClass( "stubs.ExampleClass" );
		Class<?> class2 = childLoader2.loadClass( "stubs.ExampleClass" );

		assertNotNull( class1 );
		assertNotNull( class2 );
		assertFalse( class1 == class2 );
		assertNotEquals( class1, class2 );

		childLoader1.close();
		childLoader2.close();
		parentLoader.close();
	}

	@Test
	public void testForNoJVMOverrides() throws ClassNotFoundException, IOException
	{
		// Enforce class loading by current class loader
		getClass().getClassLoader().loadClass( String.class.getName() );

		URLClassLoader parentLoader = new URLClassLoader( createURLs() );
		ChildFirstURLClassLoader childLoader = new ChildFirstURLClassLoader( createURLs( getJVMJar( "rt.jar" ) ),
				parentLoader );

		Class<?> class1 = childLoader.loadClass( String.class.getName() );

		assertTrue( class1 == String.class );
		assertEquals( class1, String.class );

		childLoader.close();
		parentLoader.close();
	}

	@Test
	public void testForParentOverrides() throws ClassNotFoundException, IOException
	{
		URLClassLoader parentLoader = new URLClassLoader( createURLs( "target/test-classes" ) );
		ChildFirstURLClassLoader childLoader = new ChildFirstURLClassLoader( createURLs( "target/test-classes" ),
				parentLoader );

		Class<?> class1 = parentLoader.loadClass( "stubs.ExampleClass" );
		Class<?> class2 = childLoader.loadClass( "stubs.ExampleClass" );

		assertNotNull( class1 );
		assertNotNull( class2 );
		assertFalse( class1 == class2 );
		assertNotEquals( class1, class2 );

		childLoader.close();
		parentLoader.close();
	}

	@Test(expected = ClassNotFoundException.class)
	public void testCurrentClassLoaderIsNotInterfeering() throws ClassNotFoundException, IOException
	{
		getClass().getClassLoader().loadClass( ExampleClass.class.getName() );

		URLClassLoader parentLoader = new URLClassLoader( createURLs(), null );

		ChildFirstURLClassLoader childLoader = new ChildFirstURLClassLoader( createURLs(), parentLoader );
		try
		{
			childLoader.loadClass( "stubs.ExampleClass" );
		}
		finally
		{
			childLoader.close();
			parentLoader.close();
		}
	}

	private String getJVMJar( String jarFile )
	{
		return File.pathSeparator + System.getProperty( "java.home" ) + File.separator + "lib" + File.separator
				+ jarFile;
	}

	private URL[] createURLs( String... paths )
	{
		URL[] urls = new URL[paths.length];
		int i = 0;
		for ( String path : paths )
		{
			try
			{
				urls[i++] = new File( path ).toURI().toURL();
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
