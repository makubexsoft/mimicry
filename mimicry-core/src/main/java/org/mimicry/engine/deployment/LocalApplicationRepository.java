package org.mimicry.engine.deployment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.mimicry.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class LocalApplicationRepository implements ApplicationRepository
{
    private static final String DEFAULT_APP_REPOSITORY;
    private static final Logger logger;
    private static final String APPLICATION_PROPERTIES;
    static
    {
        logger = LoggerFactory.getLogger(LocalApplicationRepository.class);
        APPLICATION_PROPERTIES = "application.properties";
        DEFAULT_APP_REPOSITORY = ".mimicry" + File.separator + "repository";
    }

    private final Map<String, ApplicationBundle> loadedDescriptors;
    private final File repositoryPath;

    public LocalApplicationRepository() throws IOException
    {
        this(new File(System.getProperty("user.home"), DEFAULT_APP_REPOSITORY));
    }

    public LocalApplicationRepository(File repositoryPath) throws IOException
    {
        Preconditions.checkNotNull(repositoryPath);

        this.repositoryPath = repositoryPath;
        if (!repositoryPath.exists() && !repositoryPath.mkdirs())
        {
            throw new IOException("Failed to create directories of repository: " + repositoryPath);
        }

        loadedDescriptors = new HashMap<String, ApplicationBundle>();
    }

    public static File getDefaultPath()
    {
        return new File(System.getProperty("user.home"), DEFAULT_APP_REPOSITORY);
    }

    @Override
    public Set<String> listBundles()
    {
        Set<String> appNames = new HashSet<String>();
        for (File f : repositoryPath.listFiles())
        {
            if (f.getName().endsWith(".zip"))
            {
                appNames.add(f.getName().substring(0, f.getName().length() - 4));
            }
        }
        return appNames;
    }

    @Override
    public byte[] loadBundle(String applicationName)
    {
        ApplicationBundle bundle = findBundle(applicationName);
        return IOUtils.readIntoByteArray(bundle.getLocalLocation());
    }

    private ApplicationBundle loadDescriptor(String appName) throws IOException
    {
        File bundleFile = new File(repositoryPath, appName + ".zip");
        final ZipFile zipFile = new ZipFile(bundleFile);
        try
        {
            ZipEntry entry = zipFile.getEntry(APPLICATION_PROPERTIES);
            if (entry == null)
            {
                throw new IOException("Application bundle doesn't contain a " + APPLICATION_PROPERTIES);
            }
            Properties props = new Properties();
            props.load(zipFile.getInputStream(entry));

            ApplicationBundle.Builder builder = new ApplicationBundle.Builder();
            builder.withName(appName);
            builder.withMainClass(props.getProperty("Main-Class"));
            builder.withClassPath(props.getProperty("Class-Path"));
            builder.withLocalLocation(bundleFile);
            String supportedOSs = props.getProperty("Supported-OS");
            if (supportedOSs != null && !supportedOSs.isEmpty())
            {
                for (String s : Splitter.on(",").split(supportedOSs))
                {
                    builder.withSupportedOS(s);
                }
            }
            return builder.build();
        }
        finally
        {
            IOUtils.closeSilently(zipFile);
        }
    }

    @Override
    public void storeBundle(String appName, InputStream bundleStream) throws IOException
    {
        File bundleFile = new File(repositoryPath, appName + ".zip");
        if (bundleFile.exists())
        {
            bundleFile.delete();
        }
        bundleFile.createNewFile();
        IOUtils.writeToFile(bundleStream, bundleFile);
    }

    @Override
    public ApplicationBundle findBundle(String applicationName)
    {
        ApplicationBundle descriptor = null;
        try
        {
            descriptor = loadDescriptor(applicationName);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return descriptor;
    }
}
