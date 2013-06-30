package com.gc.mimicry.core.deployment;

import java.io.File;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;

/**
 * Instances of this class contain all information necessary to deploy and instatiate application instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationDescriptor implements Serializable
{
    private static final long serialVersionUID = -6006659970679551969L;
    private transient File bundleLocation;
    private final String name;
    private final Set<String> commandLine;
    private final Set<String> classPath;
    private String mainClass;
    private String runnableJarFile;
    private final Set<String> supportedOSes;

    ApplicationDescriptor(String name)
    {
        Preconditions.checkNotNull(name);

        this.name = name;
        commandLine = new TreeSet<String>();
        classPath = new TreeSet<String>();
        supportedOSes = new TreeSet<String>();
    }

    void setBundleLocation(File bundleLocation)
    {
        this.bundleLocation = bundleLocation;
    }

    void addCommandLine(String commandLine)
    {
        this.commandLine.add(commandLine);
    }

    void addClassPath(String classPath)
    {
        this.classPath.add(classPath);
    }

    void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
    }

    void setRunnableJarFile(String runnableJarFile)
    {
        this.runnableJarFile = runnableJarFile;
    }

    void addSupportedOS(String supportedOS)
    {
        this.supportedOSes.add(supportedOS);
    }

    /**
     * Returns the location of the bundle file or null if this descriptor describes an in-memory representation of a
     * bundle.
     * 
     * @return
     */
    public File getBundleLocation()
    {
        return bundleLocation;
    }

    /**
     * Returns the name of the application.
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the full qualified name of the main class or null if it's an runnable jar.
     * 
     * @return
     */
    public String getMainClass()
    {
        return mainClass;
    }

    /**
     * Returns the name of the runnable jar file or null if the application is started using a main class.
     * 
     * @return
     */
    public String getRunnableJarFile()
    {
        return runnableJarFile;
    }

    /**
     * Returns the command line parameters.
     * 
     * @return
     */
    public Set<String> getCommandLine()
    {
        return commandLine;
    }

    /**
     * Returns the class path required to execute the application. It doesn't contain the JRE library since the location
     * is platform dependent.
     * 
     * @return
     */
    public Set<String> getClassPath()
    {
        return classPath;
    }

    /**
     * Returns a set of supported operating system or an empty if all platforms are supported (default).
     * 
     * @return
     */
    public Set<String> getSupportedOSes()
    {
        return supportedOSes;
    }

    @Override
    public String toString()
    {
        return "ApplicationDescriptor [name=" + name + ", commandLine=" + commandLine + ", classPath=" + classPath
                + ", mainClass=" + mainClass + ", runnableJarFile=" + runnableJarFile + ", supportedOSes="
                + supportedOSes + "]";
    }
}
