package test.prototypes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import stubs.ExampleClass;

public class TestClassLoading
{
    /**
     * Tests whether we can override a class already loaded by the system class loader by setting the parent to null.
     */
    @SuppressWarnings("resource")
    @Test
    public void testLoadIndependendThanSystem() throws MalformedURLException, ClassNotFoundException
    {
        URLClassLoader loader1 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);

        Class<?> class1 = loader1.loadClass(ExampleClass.class.getName());

        assertFalse(class1.equals(ExampleClass.class));
    }

    /**
     * Tests whether two class loader on the same hierarchy level load classes independently
     */
    @SuppressWarnings("resource")
    @Test
    public void testLoadIndependend() throws MalformedURLException, ClassNotFoundException
    {
        URLClassLoader loader1 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);
        URLClassLoader loader2 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);

        Class<?> class1 = loader1.loadClass(ExampleClass.class.getName());
        Class<?> class2 = loader2.loadClass(ExampleClass.class.getName());

        assertFalse(class1.equals(class2));
    }

    /**
     * Tests whether two class loader in a hierarchy load the same class.
     */
    @SuppressWarnings("resource")
    @Test
    public void testLoadByHierarchyIsSame() throws MalformedURLException, ClassNotFoundException
    {
        URLClassLoader loader1 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);
        URLClassLoader loader2 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() },
                loader1);

        Class<?> class1 = loader1.loadClass(ExampleClass.class.getName());
        Class<?> class2 = loader2.loadClass(ExampleClass.class.getName());

        assertTrue(class1.equals(class2));
    }

    @Test
    public void test() throws MalformedURLException
    {
        URLClassLoader loader1 = new URLClassLoader(new URL[] { new File("target/test-classes").toURI().toURL() }, null);

        final ExampleClass obj1 = new ExampleClass();
        Thread.currentThread().setContextClassLoader(loader1);
        Runnable r = new Runnable()
        {
            @Override
            public void run()
            {
                ExampleClass obj2 = new ExampleClass();

                assertFalse(obj1.getClass().equals(obj2.getClass()));
            }
        };
        r.run();
    }
}
