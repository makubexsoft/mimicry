package com.gc.mimicry.bridge.weaving;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gc.mimicry.util.ClassPathUtil;
import com.gc.mimicry.util.ReflectionUtil;

public class TestWeavingClassLoader
{
    @Before
    public void setUp() throws MalformedURLException
    {
        String[] classUrls = { "target/classes", "target/test-classes" };
        LoopInterceptingByteCodeLoader byteCodeLoader = new LoopInterceptingByteCodeLoader(classUrls);

        loader = new WeavingClassLoader(Arrays.asList(ClassPathUtil.createClassPath("target/classes")),
                Arrays.asList(ClassPathUtil.createClassPath("target/classes")), byteCodeLoader, null);
    }

    @After
    public void tearDown() throws IOException
    {
        loader.close();
    }

    @Test
    public void testVerifyError() throws Exception
    {
        Class<?> wovenClass = loader.loadClass(SOCKET_CLASS_NAME);
        assertNotNull(wovenClass);
    }

    @Test
    public void testLoopInterception() throws Exception
    {
        Class<?> wovenClass = loader.loadClass(EXAMPLE_CLASS_NAME);
        assertNotNull(wovenClass);

        ReflectionUtil strategyClass = ReflectionUtil.createFor(loader, STRATEGY_INTERFACE_NAME);
        ReflectionUtil counterClass = ReflectionUtil.createFor(loader, COUNTING_STRATEGY_NAME);
        ReflectionUtil interceptorClass = ReflectionUtil.createFor(loader, INTERCEPTOR_INTERFACE_NAME);

        Object counter = counterClass.newInstance();

        interceptorClass.selectMethod("setStrategy", strategyClass.getActualClass());
        interceptorClass.invokeStatic(counter);

        Object instance = wovenClass.newInstance();
        Method method = wovenClass.getDeclaredMethod("loopTenTimes");

        method.invoke(instance);

        counterClass.selectMethod("getCounter");
        int actualValue = ((Integer) counterClass.invoke(counter));

        assertTrue("ActualValue was: " + actualValue, actualValue > 0);
    }

    private static final String SOCKET_CLASS_NAME = "stubs.SimpleSocketApp";
    private static final String EXAMPLE_CLASS_NAME = "stubs.ExampleClass";
    private static final String INTERCEPTOR_INTERFACE_NAME = "com.gc.mimicry.bridge.weaving.LoopInterceptor";
    private static final String COUNTING_STRATEGY_NAME = "com.gc.mimicry.bridge.weaving.CountingInterceptionStrategy";
    private static final String STRATEGY_INTERFACE_NAME = "com.gc.mimicry.bridge.weaving.LoopInterceptionStrategy";
    private WeavingClassLoader loader;
}
