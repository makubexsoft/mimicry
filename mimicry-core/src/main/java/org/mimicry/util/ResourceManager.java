package org.mimicry.util;

import java.io.Closeable;

/**
 * A {@link ResourceManager} allows attaching {@link Closeable}s that will be closed too, when the manager is closed.
 * 
 * @author Marc-Christian Schulze
 * @see BaseResourceManager
 */
public interface ResourceManager extends Closeable
{
    /**
     * Attaches a resource to this manager that is closed when the resource manager is closed.
     * 
     * @param resource
     */
    public void attachResource(Closeable resource);

    /**
     * Tries to detach the resource from the manager if present.
     * 
     * @param resource
     * @return Whether the resource was attached to the manager.
     */
    public boolean detachResource(Closeable resource);

    /**
     * Closes all attached resources in reverse order they have been attached.
     */
    @Override
    public void close();
}
