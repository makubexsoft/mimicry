package com.gc.mimicry.bridge.weaving;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;

import org.aspectj.weaver.loadtime.WeavingURLClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gc.mimicry.util.ClassPathUtil;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;

/**
 * {@link ClassLoader} implementation that uses the {@link LoopInterceptingByteCodeLoader} for loading and
 * pre-processing byte code of a simulated application. After the byte code has been pre-processed it's forwarded to
 * aspectJ in order to apply more aspects.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class WeavingClassLoader extends WeavingURLClassLoader
{
    private static HashSet<String> forbiddenPackages = new HashSet<String>();
    static
    {
        forbiddenPackages.add("com.gc.mimicry");
        forbiddenPackages.add("java");
        forbiddenPackages.add("sun");
    }

    private URLClassLoader sharedAppLoader;

    public WeavingClassLoader(Collection<URL> classPath, Collection<URL> aspects,
            LoopInterceptingByteCodeLoader loader, ClassLoader parent) throws MalformedURLException
    {
        super(classPath.toArray(new URL[0]), aspects.toArray(new URL[0]), parent);

        Preconditions.checkNotNull(aspects);
        Preconditions.checkNotNull(loader);
        this.loader = loader;

        Collection<URL> urls = Collections2.transform(ClassPathUtil.getSystemClassPath(), new Function<String, URL>()
        {
            @Override
            public URL apply(String f)
            {
                try
                {
                    return new File(f).toURI().toURL();
                }
                catch (MalformedURLException e)
                {
                    return null;
                }
            }
        });
        urls = new HashSet<URL>(urls);
        urls.addAll(classPath);

        sharedAppLoader = new WeavingURLClassLoader(urls.toArray(new URL[0]), aspects.toArray(new URL[0]), null /*
                                                                                                                 * prevent
                                                                                                                 * CL
                                                                                                                 * from
                                                                                                                 * loading
                                                                                                                 * JRE
                                                                                                                 * classes
                                                                                                                 */)
        {
            // @Override
            // public Class<?> loadClass(String name) throws ClassNotFoundException
            // {
            //
            // return super.loadClass(name);
            // }

            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException
            {
                if (name.startsWith("com.gc.mimicry."))
                {
                    return WeavingClassLoader.super.loadClass(name);
                }
                // System.out.println("[SHARED] " + name);
                // FIXME: the shared loader does NOT weave!!!
                return super.findClass(name);
            }
        };
        // System.out.println("SHARE = " + sharedAppLoader);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null)
        {
            return clazz;
        }

        try
        {
            // First check our class path which contains the BRIDGE and ASPECTS
            Class c = findClass(name);
            // System.out.println("[BRIDGE] " + name);
            return c;
        }
        catch (ClassNotFoundException e)
        {
            // System.out.println("[!! BRIDGE !!] " + name);
            // if it's no mimicry class we check a duplicated system class path
            // to allow reloading classes embedded in the application
            if (!name.startsWith("com.gc.mimicry."))
            {
                try
                {
                    Class c = sharedAppLoader.loadClass(name);
                    // System.out.println("[SHARED] " + name + " (" + c + ")");
                    return c;
                }
                catch (ClassNotFoundException ex)
                {
                    // now we need to load from parent
                }
            }
            // System.out.println("[SYSTEM] " + name);
            // It must be a class of the JRE or Mimicry itself
            return super.loadClass(name);
        }
    }

    /**
     * First queries the {@link LoopInterceptingByteCodeLoader} to load the byte code if it doesn't find the class
     * (because it's not part of simulated application) the call is forwarded to the actual implementation of the base
     * class.
     */
    // @Override
    // protected byte[] getBytes(String name) throws IOException
    // {
    // byte[] byteCode = super.getBytes(name);
    // if (byteCode == null)
    // {
    // // for (String pkg : forbiddenPackages)
    // // {
    // // if (name.startsWith(pkg + "."))
    // // {
    // // return null;
    // // }
    // // }
    // // //byteCode = loader.loadTransformedByteCode(name);
    // // if (byteCode != null)
    // // {
    // // logger.debug("Soot loaded byte code: " + name + " (" + byteCode.length + " bytes)");
    // // }
    // }
    // else if (logger.isDebugEnabled())
    // {
    // logger.debug("AspectJ loaded byte code: " + name + " (" + byteCode.length + " bytes)");
    // }
    // // IOUtils.writeToFile(byteCode, new File("_dump_" + name + ".class"));
    // return byteCode;
    // }

    static
    {
        logger = LoggerFactory.getLogger(WeavingClassLoader.class);
    }
    private final LoopInterceptingByteCodeLoader loader;
    private static Logger logger;
}
