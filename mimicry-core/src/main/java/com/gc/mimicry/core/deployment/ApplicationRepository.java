package com.gc.mimicry.core.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface ApplicationRepository
{

	public Set<String> getApplicationNames();

	public ApplicationDescriptor getApplicationDescriptor( String applicationName );

	public void storeApplication( String appName, InputStream bundleStream ) throws IOException;
}
