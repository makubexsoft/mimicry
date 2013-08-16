package com.gc.mimicry.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Splitter;

public class ClassPathUtil
{

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

    public static Set<String> getSystemClassPath()
    {
        Set<String> result = new HashSet<String>();
        String path = System.getProperty("java.class.path");
        Iterable<String> split = Splitter.on(File.pathSeparatorChar).split(path);
        for (String s : split)
        {
            result.add(s);
        }
        return result;
    }
}
