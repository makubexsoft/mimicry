package com.gc.mimicry.ext.stdio;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import com.gc.mimicry.engine.EventBroker;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.ext.stdio.events.ConsoleOutputEvent;
import com.gc.mimicry.ext.stdio.events.ConsoleStderrEvent;
import com.gc.mimicry.ext.stdio.events.ConsoleStdoutEvent;
import com.gc.mimicry.util.ByteBuffer;
import com.google.common.base.Preconditions;

public class ConsoleInputStream extends InputStream
{
    private final EventBroker eventBroker;
    private final UUID applicationId;
    private final EventReceiver receiver;
    private final ByteBuffer buffer;
    private final InputStream bufferStream;
    private final boolean stdout;

    private ConsoleInputStream(EventBroker eventBroker, UUID applicationId, boolean stdout)
    {
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(applicationId);

        this.eventBroker = eventBroker;
        this.applicationId = applicationId;
        this.stdout = stdout;

        buffer = new ByteBuffer();
        bufferStream = buffer.createStream();

        receiver = new EventReceiver();
        eventBroker.addEventListener(receiver);
    }

    public static ConsoleInputStream attachStdout(EventBroker eventBroker, UUID applicationId)
    {
        return new ConsoleInputStream(eventBroker, applicationId, true);
    }

    public static ConsoleInputStream attachStderr(EventBroker eventBroker, UUID applicationId)
    {
        return new ConsoleInputStream(eventBroker, applicationId, false);
    }

    @Override
    public int read() throws IOException
    {
        return bufferStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return bufferStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return bufferStream.read(b, off, len);
    }

    @Override
    public int available() throws IOException
    {
        return bufferStream.available();
    }

    @Override
    public void close() throws IOException
    {
        eventBroker.removeEventListener(receiver);
        super.close();
    }

    private class EventReceiver implements EventListener
    {
        @Override
        public void handleEvent(Event evt)
        {
            if (stdout && evt instanceof ConsoleStdoutEvent)
            {
                ConsoleStdoutEvent e = (ConsoleStdoutEvent) evt;
                handleConsoleOutput(e);
            }
            else if (!stdout && evt instanceof ConsoleStderrEvent)
            {
                ConsoleStderrEvent e = (ConsoleStderrEvent) evt;
                handleConsoleOutput(e);
            }
        }

        private void handleConsoleOutput(ConsoleOutputEvent e)
        {
            if (e.getSourceApplication().equals(applicationId))
            {
                buffer.write(e.getData());
            }
        }
    }
}
