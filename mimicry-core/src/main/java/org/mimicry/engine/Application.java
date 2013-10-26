package org.mimicry.engine;

import java.util.UUID;

import org.mimicry.util.concurrent.Future;


public interface Application
{
    public void start(String... commandArgs);

    public UUID getId();

    public Future<?> stop();

    public Future<?> getTerminationFuture();
}
