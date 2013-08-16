package com.gc.mimicry.cluster.session;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.cluster.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.engine.timing.Clock;
import com.gc.mimicry.engine.timing.Scheduler;

public interface Session extends Closeable
{

    public UUID getId();

    public SessionLocalMessagingService getMessagingService();

    public Scheduler getScheduler();

    public Clock getClock();

    public void close();
}