package org.mimicry.bundle;

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
    public Set<String> listBundles();

    public ApplicationBundle findBundle(String applicationName);

    public byte[] loadBundle(String applicationName);

    public void storeBundle(String appName, InputStream bundleStream) throws IOException;
}
