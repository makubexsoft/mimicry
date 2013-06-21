package com.gc.mimicry.bridge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import soot.G;

public class TestSootByteCodeLoader
{
	@Before
	public void setUp()
	{
		G.reset();
	}

	@Test
	public void testCanLoadByteCode() throws IOException
	{
		SootByteCodeLoader loader = new SootByteCodeLoader( new String[] {
				new File( "target/classes" ).getAbsolutePath(), new File( "target/test-classes" ).getAbsolutePath() } );

		byte[] bytes = loader.loadBytes( "stubs.ExampleClass" );

		assertNotNull( bytes );
	}

	@Test
	public void testReturnsNullIfClassNotFound() throws IOException
	{
		SootByteCodeLoader loader = new SootByteCodeLoader(
				new String[] { new File( "target/classes" ).getAbsolutePath() } );

		byte[] bytes = loader.loadBytes( "stubs.ExampleClass" );

		assertNull( bytes );
	}

	@Test(expected = RuntimeException.class)
	public void testRequiresAtLeastTheLoopInterceptor() throws IOException
	{
		new SootByteCodeLoader( new String[] {} );
	}
}
