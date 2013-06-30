package com.gc.mimicry.core.deployment;

import java.io.File;

import com.google.common.base.Splitter;

/**
 * A utility class implementing the builder pattern for the {@link ApplicationDescriptor}.
 * 
 * @author Marc-Christian Schulze
 * @see ApplicationDescriptor
 */
public class ApplicationDescriptorBuilder
{
    public static ApplicationDescriptorBuilder newDescriptor(String name)
    {
        return new ApplicationDescriptorBuilder(name);
    }

    private ApplicationDescriptorBuilder(String name)
    {
        desc = new ApplicationDescriptor(name);
    }

    public ApplicationDescriptorBuilder withBundleLocation(File bundleLocation)
    {
        desc.setBundleLocation(bundleLocation);
        return this;
    }

    public ApplicationDescriptorBuilder withCommandLine(String cmd)
    {
        for (String s : Splitter.on(" ").split(cmd))
        {
            desc.addCommandLine(s);
        }
        return this;
    }

    public ApplicationDescriptorBuilder withClassPath(String path)
    {
        for (String s : Splitter.on(File.pathSeparator).split(path))
        {
            desc.addClassPath(s);
        }
        return this;
    }

    public ApplicationDescriptorBuilder withMainClass(String name)
    {
        desc.setMainClass(name);
        return this;
    }

    public ApplicationDescriptorBuilder withRunnableJar(String name)
    {
        desc.setRunnableJarFile(name);
        return this;
    }

    public ApplicationDescriptorBuilder withSupportedOS(String osName)
    {
        desc.addSupportedOS(osName);
        return this;
    }

    public ApplicationDescriptor build()
    {
        return desc;
    }

    private final ApplicationDescriptor desc;
}
