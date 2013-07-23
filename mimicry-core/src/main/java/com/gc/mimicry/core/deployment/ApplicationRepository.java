package com.gc.mimicry.core.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * An {@link ApplicationRepository} is comparable to a maven repository. It contains deployable application bundles.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public interface ApplicationRepository
{

    public Set<String> getApplicationNames();

    public ApplicationBundleDescriptor getApplicationDescriptor(String applicationName);

    public void storeApplication(String appName, InputStream bundleStream) throws IOException;
}
