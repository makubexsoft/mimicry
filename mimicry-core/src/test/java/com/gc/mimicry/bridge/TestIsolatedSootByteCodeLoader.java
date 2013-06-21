package com.gc.mimicry.bridge;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestIsolatedSootByteCodeLoader
{

	@Test
	public void test()
	{
		LoopInterceptingByteCodeLoader loader = new LoopInterceptingByteCodeLoader( new String[] { "./target/classes",
				"./target/test-classes" } );

		byte[] byteCode = loader.loadTransformedByteCode( "stubs.ExampleClass" );

		assertNotNull( byteCode );
	}
}
