package com.gc.mimicry.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * IO related utility functionality.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class IOUtils
{
    /**
     * Closed the given closeable and suppress any {@link IOException} silently.
     * 
     * @param c
     *            The closeable to close.
     */
    public static void closeSilently(Closeable c)
    {
        if (c != null)
        {
            try
            {
                c.close();
            }
            catch (IOException e)
            {
                // suppress
            }
        }
    }

    /**
     * Creates a temporary directory.
     * 
     * @param prefix
     *            The prefix to use for the directory name
     * @param suffix
     *            The suffix to use for the directory name.
     * @return A file instance that refers to the newly created directory.
     * @throws IOException
     *             If any io operation fails and the directory couldn't be created.
     */
    public static File createTempDir(String prefix, String suffix) throws IOException
    {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.delete();
        tempFile.mkdirs();
        return tempFile;
    }

    /**
     * Writes all data within the given stream into the destination file.
     * 
     * @param stream
     *            The stream to read the content from.
     * @param destination
     *            The file to save the content in.
     * @throws IOException
     *             If the write operation fails.
     */
    public static void writeToFile(InputStream stream, File destination) throws IOException
    {
        if (!destination.exists())
        {
            destination.createNewFile();
        }
        BufferedOutputStream out = null;
        try
        {
            out = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buffer = new byte[4096];
            int read = -1;
            while ((read = stream.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }
        }
        finally
        {
            closeSilently(out);
        }
    }

    public static List<File> collectFiles(File path, FilenameFilter filter)
    {
        List<File> result = new ArrayList<File>();
        File[] files = path.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isFile() && filter.accept(path, f.getName()))
                {
                    result.add(f);
                }
                else
                {
                    result.addAll(collectFiles(f, filter));
                }
            }
        }
        return result;
    }

    /**
     * Writes all data within the given byte array into the destination file.
     * 
     * @param data
     *            The data to write to the file.
     * @param destination
     *            The file to write to.
     * @throws IOException
     *             If the write fails
     */
    public static void writeToFile(byte[] data, File destination) throws IOException
    {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        try
        {
            writeToFile(in, destination);
        }
        finally
        {
            in.close();
        }
    }

    /**
     * Reads the whole file content into a byte array and returns it.
     * 
     * @param file
     *            The file to read from.
     * @return The byte array containing the content; otherwise an empty byte array.
     */
    public static byte[] readIntoByteArray(File file)
    {
        ByteArrayOutputStream out = null;
        BufferedInputStream in = null;
        try
        {
            out = new ByteArrayOutputStream();
            in = new BufferedInputStream(new FileInputStream(file));

            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }

            return out.toByteArray();
        }
        catch (IOException e)
        {
            return new byte[0];
        }
        finally
        {
            closeSilently(in);
            closeSilently(out);
        }
    }

    public static void deleteRecursivly(File workspace)
    {
        for (File file : workspace.listFiles())
        {
            if (file.isDirectory())
            {
                deleteRecursivly(file);
            }
            else
            {
                file.delete();
            }
        }
    }
}
