package com.gc.mimicry.util;

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
    public void testChildFirstOverridesParent() throws MalformedURLException, ClassNotFoundException
    {
        URLClassLoader loader1 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);
        URLClassLoader loader2 = new MeFirstClassLoader(new URL[] { new File("target/test-classes").toURI()
                .toURL() }, loader1);

        Class<?> class1 = loader1.loadClass(ExampleClass.class.getName());
        Class<?> class2 = loader2.loadClass(ExampleClass.class.getName());

        assertFalse(class1.equals(class2));
    }

    @Test
    public void testParentRecovers() throws ClassNotFoundException, IOException
    {
        URLClassLoader parentLoader = new URLClassLoader(createURLs("target/test-classes"));
        MeFirstClassLoader childLoader1 = new MeFirstClassLoader(createURLs(), parentLoader);
        MeFirstClassLoader childLoader2 = new MeFirstClassLoader(createURLs(), parentLoader);

        Class<?> class1 = childLoader1.loadClass(ExampleClass.class.getName());
        Class<?> class2 = childLoader2.loadClass(ExampleClass.class.getName());

        assertNotNull(class1);
        assertNotNull(class2);
        assertEquals(class1, class2);

        childLoader1.close();
        childLoader2.close();
        parentLoader.close();
    }

    @Test
    public void testChildLoadsFirst() throws ClassNotFoundException, IOException
    {
        URLClassLoader parentLoader = new URLClassLoader(createURLs("target/test-classes"));
        MeFirstClassLoader childLoader1 = new MeFirstClassLoader(createURLs("target/test-classes"),
                parentLoader);
        MeFirstClassLoader childLoader2 = new MeFirstClassLoader(createURLs("target/test-classes"),
                parentLoader);

        Class<?> class1 = childLoader1.loadClass(ExampleClass.class.getName());
        Class<?> class2 = childLoader2.loadClass(ExampleClass.class.getName());

        assertNotNull(class1);
        assertNotNull(class2);
        assertFalse(class1 == class2);
        assertNotEquals(class1, class2);

        childLoader1.close();
        childLoader2.close();
        parentLoader.close();
    }

    @Test
    public void testForNoJVMOverrides() throws ClassNotFoundException, IOException
    {
        // Enforce class loading by current class loader
        getClass().getClassLoader().loadClass(String.class.getName());

        URLClassLoader parentLoader = new URLClassLoader(createURLs());
        MeFirstClassLoader childLoader = new MeFirstClassLoader(createURLs(getJVMJar("rt.jar")),
                parentLoader);

        Class<?> class1 = childLoader.loadClass(String.class.getName());

        assertTrue(class1 == String.class);
        assertEquals(class1, String.class);

        childLoader.close();
        parentLoader.close();
    }

    @Test
    public void testForParentOverrides() throws ClassNotFoundException, IOException
    {
        URLClassLoader parentLoader = new URLClassLoader(createURLs("target/test-classes"));
        MeFirstClassLoader childLoader = new MeFirstClassLoader(createURLs("target/test-classes"),
                parentLoader);

        Class<?> class1 = parentLoader.loadClass(ExampleClass.class.getName());
        Class<?> class2 = childLoader.loadClass(ExampleClass.class.getName());

        assertNotNull(class1);
        assertNotNull(class2);
        assertFalse(class1 == class2);
        assertNotEquals(class1, class2);

        childLoader.close();
        parentLoader.close();
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCurrentClassLoaderIsNotInterfeering() throws ClassNotFoundException, IOException
    {
        getClass().getClassLoader().loadClass(ExampleClass.class.getName());

        URLClassLoader parentLoader = new URLClassLoader(createURLs(), null);

        MeFirstClassLoader childLoader = new MeFirstClassLoader(createURLs(), parentLoader);
        try
        {
            childLoader.loadClass(ExampleClass.class.getName());
        }
        finally
        {
            childLoader.close();
            parentLoader.close();
        }
    }

    private String getJVMJar(String jarFile)
    {
        return File.pathSeparator + System.getProperty("java.home") + File.separator + "lib" + File.separator + jarFile;
    }

    private URL[] createURLs(String... paths)
    {
        URL[] urls = new URL[paths.length];
        int i = 0;
        for (String path : paths)
        {
            try
            {
                urls[i++] = new File(path).toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return urls;
    }
}
