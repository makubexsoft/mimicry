package com.gc.mimicry.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;


public class DirectoryBasedClassLoader extends URLClassLoader
{
    public DirectoryBasedClassLoader(File pluginDirectory)
    {
        super(listPlugins(pluginDirectory), Thread.currentThread().getContextClassLoader());
    }

    private static URL[] listPlugins(File directory)
    {
        File[] jarFiles = directory.listFiles(new FileNameExtensionFilter(".jar"));
        URL[] urls = new URL[jarFiles.length + 1];
        urls[0] = toURL(directory.toURI());
        for (int i = 0; i < jarFiles.length; i++)
        {
            urls[i + 1] = toURL(jarFiles[i].toURI());
        }
        return urls;
    }

    private static URL toURL(URI uri)
    {
        try
        {
            return uri.toURL();
        }
        catch (MalformedURLException e)
        {
            // should not happen
            throw new RuntimeException("", e);
        }
    }
}
