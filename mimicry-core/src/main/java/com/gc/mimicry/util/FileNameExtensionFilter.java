package com.gc.mimicry.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * {@link FilenameFilter} that is based on the file extension.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class FileNameExtensionFilter implements java.io.FilenameFilter
{
    /**
     * Constructs a new filter with the given file extension. Prepend a dot to the extension to reduce false-positive
     * matches, e.g. ".txt"
     * 
     * @param extension
     */
    public FileNameExtensionFilter(String extension)
    {
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name)
    {
        if (new File(dir, name).isDirectory())
        {
            return true;
        }
        return name.toLowerCase().endsWith(extension.toLowerCase());
    }

    private final String extension;
}
