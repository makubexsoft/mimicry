package org.mimicry.engine.deployment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.mimicry.util.IOUtils;

import com.google.common.base.Preconditions;

public class BundleCreator
{
    private final String bundleName;
    private final List<String> classPath;
    private String mainClass;
    private final List<String> supportedOSes;
    private final List<FileEntry> content;
    private ZipOutputStream stream;

    public BundleCreator(String bundleName)
    {
        Preconditions.checkNotNull(bundleName);
        this.bundleName = bundleName;

        classPath = new ArrayList<String>();
        supportedOSes = new ArrayList<String>();
        content = new ArrayList<FileEntry>();
    }

    /**
     * Specifies the name of the class containing the public entry point "public static void main(String[])" that should
     * be used when starting the application.
     * 
     * @param fullQualifiedClassName
     *            The fully-qualified name of the class containing the public entry point
     *            "public static void main(String[])" that should be used when starting the application.
     */
    public void setMainClass(String fullQualifiedClassName)
    {
        mainClass = fullQualifiedClassName;
    }

    public void addClassPathEntry(String classPathEntry)
    {
        classPath.add(classPathEntry);
    }

    public void addSupportedOperatingSystem(String osName)
    {
        supportedOSes.add(osName);
    }

    /**
     * Adds a file or directory to the bundle. This method does not immediately perform any IO operations.
     * 
     * @param fileOrDirectory
     *            The file or directory to copy into the bundle. If the file points to a directory all sub files are
     *            copied as well.
     * @param path
     *            The path under which the file or directory content should be copied. Please make sure you use the
     *            appropriate path separator (slash - /).
     */
    public void addFile(File fileOrDirectory, String path)
    {
        content.add(new FileEntry(fileOrDirectory, path));
    }

    /**
     * Creates the bundle within the specified directory and returns the created file.
     * 
     * @param targetDirectory
     *            The directory in which the bundle should be written.
     * @return A file that points to the newly created bundle file.
     * @throws IOException
     *             If an IO error occurred during writing the bundle file.
     */
    public File createBundle(File targetDirectory) throws IOException
    {
        File bundleFile = getBundleFile(targetDirectory);

        stream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(bundleFile)));
        writeContent();
        stream.close();
        stream = null;

        return bundleFile;
    }

    private void writeContent() throws IOException
    {
        for (FileEntry entry : content)
        {
            writeEntry(entry.sourceFile, entry.targetPath);
        }
        writeManifest();
    }

    private void writeManifest() throws IOException
    {
        BundleManifest manifest = new BundleManifest(bundleName);
        manifest.setMainClass(mainClass);
        for (String cp : classPath)
        {
            manifest.addToClassPath(cp);
        }
        for (String osName : supportedOSes)
        {
            manifest.addSupportedOperatingSystem(osName);
        }

        stream.putNextEntry(new ZipEntry("application.properties"));
        manifest.write(stream);
        stream.closeEntry();
    }

    private void writeEntry(File sourceFile, String path) throws IOException
    {
        if (sourceFile.isDirectory())
        {
            File[] files = sourceFile.listFiles();
            for (File subFile : files)
            {
                writeEntry(subFile, path + File.separator + subFile.getName());
            }
        }
        else
        {
            writeFile(sourceFile, path);
        }
    }

    private void writeFile(File file, String path) throws IOException
    {
        stream.putNextEntry(new ZipEntry(path));
        stream.write(IOUtils.readIntoByteArray(file));
        stream.closeEntry();
    }

    private File getBundleFile(File directory)
    {
        return new File(directory, bundleName + ".zip");
    }
}

class FileEntry
{
    public File sourceFile;
    public String targetPath;

    public FileEntry(File sourceFile, String targetPath)
    {
        this.sourceFile = sourceFile;
        this.targetPath = targetPath;
    }
}
