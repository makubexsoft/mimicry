package org.mimicry.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Splitter;

public class ClassPathUtil
{
    public static String getResourceLocation(ClassLoader loader, String resourceName)
    {
        URL resource = loader.getResource(resourceName);
        if (resource == null)
        {
            return null;
        }

        if (resource.getProtocol().equalsIgnoreCase("jar"))
        {
            return resource.getPath().substring(0, resource.getPath().indexOf("!"));
        }
        else if (resource.getProtocol().equalsIgnoreCase("file"))
        {
            return resource.getPath().substring(0, resource.getPath().length() - resourceName.length());
        }
        return null;
    }

    public static URL[] createClassPath(Collection<String> paths)
    {
        URL[] urls = new URL[paths.size()];
        int i = 0;
        for (String u : paths)
        {
            try
            {
                urls[i++] = new File(u).toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static URL[] createClassPath(String... paths)
    {
        URL[] urls = new URL[paths.length];
        int i = 0;
        for (String u : paths)
        {
            try
            {
                urls[i++] = new File(u).toURI().toURL();
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static Set<URL> getSystemClassPath()
    {
        Set<URL> result = new HashSet<URL>();
        String path = System.getProperty("java.class.path");
        Iterable<String> split = Splitter.on(File.pathSeparatorChar).split(path);
        for (String s : split)
        {
            try
            {
                result.add(new File(s).toURI().toURL());
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
}
