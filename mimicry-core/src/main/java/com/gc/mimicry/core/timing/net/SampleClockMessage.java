package com.gc.mimicry.core.timing.net;

import java.util.UUID;

import com.gc.mimicry.core.messaging.Message;

public class SampleClockMessage extends Message
{
    private static final long serialVersionUID = -324461336109726425L;
    private final UUID id;
    private final long deltaMillis;

    public SampleClockMessage(long deltaMillis)
    {
        id = UUID.randomUUID();
        this.deltaMillis = deltaMillis;
    }

    public long getDeltaMillis()
    {
        return deltaMillis;
    }

    public UUID getId()
    {
        return id;
    }
}
