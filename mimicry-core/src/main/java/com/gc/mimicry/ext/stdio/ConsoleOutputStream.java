package com.gc.mimicry.ext.stdio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import com.gc.mimicry.engine.EventEngine;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.ext.stdio.events.ConsoleStdinEvent;
import com.google.common.base.Preconditions;

public class ConsoleOutputStream extends OutputStream
{
    private final EventFactory eventFactory;
    private final EventEngine eventBroker;
    private final UUID applicationId;

    private ConsoleOutputStream(EventFactory eventFactory, EventEngine eventBroker, UUID applicationId)
    {
        Preconditions.checkNotNull(eventFactory);
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(applicationId);

        this.eventFactory = eventFactory;
        this.eventBroker = eventBroker;
        this.applicationId = applicationId;
    }

    public static ConsoleOutputStream attachStdin(EventFactory eventFactory, EventEngine eventBroker, UUID applicationId)
    {
        return new ConsoleOutputStream(eventFactory, eventBroker, applicationId);
    }

    @Override
    public void write(int b) throws IOException
    {
        send(new byte[] { (byte) b });
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        send(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        send(Arrays.copyOfRange(b, off, off + len));
    }

    private void send(byte[] data)
    {
        ConsoleStdinEvent event = eventFactory.createEvent(ConsoleStdinEvent.class, applicationId);
        event.setData(data);
        eventBroker.fireEvent(event);
    }
}
