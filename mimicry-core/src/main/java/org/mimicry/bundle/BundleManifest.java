package org.mimicry.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class BundleManifest
{
    private final String name;
    private final List<String> classPath;
    private String mainClass;
    private final List<String> supportedOSes;

    public BundleManifest(String name)
    {
        Preconditions.checkNotNull(name);
        this.name = name;

        classPath = new ArrayList<String>();
        supportedOSes = new ArrayList<String>();
    }

    public static BundleManifest read(InputStream in) throws IOException
    {
        Properties props = new Properties();
        props.load(in);

        BundleManifest manifest = new BundleManifest(props.getProperty("Bundle-Name"));
        manifest.setMainClass(props.getProperty("Main-Class"));
        Iterable<String> split = Splitter.on(",").split(props.getProperty("Class-Path"));
        for (String entry : split)
        {
            manifest.addToClassPath(entry.trim());
        }
        String supportedOSs = props.getProperty("Supported-OS");
        if (supportedOSs != null && !supportedOSs.isEmpty())
        {
            for (String s : Splitter.on(",").split(supportedOSs))
            {
                manifest.addSupportedOperatingSystem(s.trim());
            }
        }

        return manifest;
    }

    public void write(OutputStream out) throws IOException
    {
        Properties props = new Properties();

        props.setProperty("Bundle-Name", name);
        props.setProperty("Main-Class", mainClass);
        props.setProperty("Class-Path", Joiner.on(",").join(classPath));
        props.setProperty("Supported-OS", Joiner.on(",").join(supportedOSes));

        props.store(out, "");
    }

    public String getName()
    {
        return name;
    }

    public List<String> getClassPath()
    {
        return classPath;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public List<String> getSupportedOSes()
    {
        return supportedOSes;
    }

    public void setMainClass(String fullyQualifiedClassName)
    {
        mainClass = fullyQualifiedClassName;
    }

    public void addSupportedOperatingSystem(String osName)
    {
        supportedOSes.add(osName);
    }

    public void addToClassPath(String classPathEntry)
    {
        classPath.add(classPathEntry);
    }
}
