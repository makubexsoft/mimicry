package com.gc.mimicry.core.session;

import java.io.Closeable;
import java.util.UUID;

import com.gc.mimicry.core.session.msg.SessionLocalMessagingService;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.Scheduler;

public interface Session extends Closeable
{

    public UUID getId();

    public SessionLocalMessagingService getMessagingService();

    public Scheduler getScheduler();

    public Clock getClock();

    public void close();
}
