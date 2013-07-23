package com.gc.mimicry.core.deployment;

import java.io.File;

import com.google.common.base.Splitter;

/**
 * A utility class implementing the builder pattern for the {@link ApplicationBundleDescriptor}.
 * 
 * @author Marc-Christian Schulze
 * @see ApplicationBundleDescriptor
 */
public class ApplicationDescriptorBuilder
{
    public static ApplicationDescriptorBuilder newDescriptor(String name)
    {
        return new ApplicationDescriptorBuilder(name);
    }

    private ApplicationDescriptorBuilder(String name)
    {
        desc = new ApplicationBundleDescriptor(name);
    }

    public ApplicationDescriptorBuilder withBundleLocation(File bundleLocation)
    {
        desc.setBundleLocation(bundleLocation);
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

    public ApplicationBundleDescriptor build()
    {
        return desc;
    }

    private final ApplicationBundleDescriptor desc;
}
