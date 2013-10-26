package org.mimicry.engine.deployment;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Instances of this class contain all information necessary to deploy and instantiate application instances.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public class ApplicationBundle implements Serializable
{
    private static final long serialVersionUID = -6006659970679551969L;
    private transient File localLocation;
    private final String name;
    private final List<String> classPath;
    private String mainClass;
    private final List<String> supportedOSes;

    public ApplicationBundle(String name)
    {
        Preconditions.checkNotNull(name);

        this.name = name;
        classPath = new ArrayList<String>();
        supportedOSes = new ArrayList<String>();
    }

    private ApplicationBundle(Builder builder)
    {
        Preconditions.checkNotNull(builder);

        this.name = builder.name;
        this.classPath = builder.classPath;
        this.mainClass = builder.mainClass;
        this.supportedOSes = builder.supportedOSes;
        this.localLocation = builder.localLocation;
    }

    void setLocalLocation(File loc)
    {
        localLocation = loc;
    }

    void addClassPath(String classPath)
    {
        this.classPath.add(classPath);
    }

    void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
    }

    void addSupportedOS(String supportedOS)
    {
        this.supportedOSes.add(supportedOS);
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

    public File getLocalLocation()
    {
        return localLocation;
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
     * Returns the class path required to execute the application. It doesn't contain the JRE library since the location
     * is platform dependent.
     * 
     * @return
     */
    public List<String> getClassPath()
    {
        return classPath;
    }

    /**
     * Returns a set of supported operating system or an empty if all platforms are supported (default).
     * 
     * @return
     */
    public List<String> getSupportedOSes()
    {
        return supportedOSes;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ApplicationBundleDescriptor [name=");
        builder.append(name);
        builder.append(", classPath=");
        builder.append(classPath);
        builder.append(", mainClass=");
        builder.append(mainClass);
        builder.append(", supportedOSes=");
        builder.append(supportedOSes);
        builder.append("]");
        return builder.toString();
    }

    public static class Builder
    {
        private String name;
        private final List<String> classPath;
        private String mainClass;
        private final List<String> supportedOSes;
        private File localLocation;

        public Builder()
        {
            classPath = new ArrayList<String>();
            supportedOSes = new ArrayList<String>();
        }

        public Builder withLocalLocation(File loc)
        {
            localLocation = loc;
            return this;
        }

        public Builder withName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder withClassPath(String cpEntry)
        {
            classPath.add(cpEntry);
            return this;
        }

        public Builder withMainClass(String className)
        {
            this.mainClass = className;
            return this;
        }

        public Builder withSupportedOS(String os)
        {
            this.supportedOSes.add(os);
            return this;
        }

        public ApplicationBundle build()
        {
            return new ApplicationBundle(this);
        }
    }
}
