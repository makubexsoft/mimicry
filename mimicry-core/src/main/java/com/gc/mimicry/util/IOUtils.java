package com.gc.mimicry.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils
{

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

    public static File createTempDir(String prefix, String suffix) throws IOException
    {
        File tempFile = File.createTempFile(prefix, suffix);
        tempFile.delete();
        tempFile.mkdirs();
        return tempFile;
    }

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
}
