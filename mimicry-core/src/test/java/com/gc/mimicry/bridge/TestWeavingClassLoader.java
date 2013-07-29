package com.gc.mimicry.bridge;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.bridge.weaving.LoopInterceptingByteCodeLoader;
import com.gc.mimicry.bridge.weaving.WeavingClassLoader;
import com.gc.mimicry.util.ReflectionUtil;

public class TestWeavingClassLoader
{
	@Before
	public void setUp() throws MalformedURLException
	{
		String[] classUrls = { "./target/classes", "./target/test-classes" };
		LoopInterceptingByteCodeLoader byteCodeLoader = new LoopInterceptingByteCodeLoader( classUrls );
		loader = new WeavingClassLoader( Collections.<URL> emptyList(), Collections.<URL> emptyList(), byteCodeLoader,
				null );
	}

	@After
	public void tearDown() throws IOException
	{
		loader.close();
	}

	@Test
	public void testCanLoad() throws Exception
	{
		Class<?> wovenClass = loader.loadClass( EXAMPLE_CLASS_NAME );
		assertNotNull( wovenClass );
	}

	@Test
	public void testLoopInterception() throws Exception
	{
		Class<?> wovenClass = loader.loadClass( EXAMPLE_CLASS_NAME );
		assertNotNull( wovenClass );

		ReflectionUtil strategyClass = ReflectionUtil.createFor( loader, STRATEGY_INTERFACE_NAME );
		ReflectionUtil counterClass = ReflectionUtil.createFor( loader, COUNTING_STRATEGY_NAME );
		ReflectionUtil interceptorClass = ReflectionUtil.createFor( loader, INTERCEPTOR_INTERFACE_NAME );

		Object counter = counterClass.newInstance();

		interceptorClass.selectMethod( "setStrategy", strategyClass.getActualClass() );
		interceptorClass.invokeStatic( counter );

		Object instance = wovenClass.newInstance();
		Method method = wovenClass.getDeclaredMethod( "loopTenTimes" );

		method.invoke( instance );

		counterClass.selectMethod( "getCounter" );
		int actualValue = ((Integer) counterClass.invoke( counter ));

		assertTrue( "ActualValue was: " + actualValue, actualValue > 0 );
	}

	private static final String	EXAMPLE_CLASS_NAME			= "stubs.ExampleClass";
	private static final String	INTERCEPTOR_INTERFACE_NAME	= "com.gc.mimicry.bridge.LoopInterceptor";
	private static final String	COUNTING_STRATEGY_NAME		= "com.gc.mimicry.bridge.CountingInterceptionStrategy";
	private static final String	STRATEGY_INTERFACE_NAME		= "com.gc.mimicry.bridge.LoopInterceptionStrategy";
	private WeavingClassLoader	loader;
}
