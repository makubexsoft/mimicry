package org.mimicry.ext.stdio;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Event;
import org.mimicry.cep.Query;
import org.mimicry.cep.QueryListener;
import org.mimicry.util.ByteBuffer;

import com.google.common.base.Preconditions;

public class ConsoleInputStream extends InputStream
{
    private final ByteBuffer buffer;
    private final InputStream bufferStream;
    private Query query;

    private ConsoleInputStream(CEPEngine eventBroker, UUID applicationId, boolean stdout)
    {
        Preconditions.checkNotNull(eventBroker);
        Preconditions.checkNotNull(applicationId);

        buffer = new ByteBuffer();
        bufferStream = buffer.createStream();

        if (stdout)
        {
            query = eventBroker.addQuery("from StdOut[appId = '" + applicationId.toString() + "']");
        }
        else
        {
            query = eventBroker.addQuery("from StdErr[appId = '" + applicationId.toString() + "']");
        }
        query.addQueryListener(new EventReceiver());
    }

    public static ConsoleInputStream attachStdout(CEPEngine eventBroker, UUID applicationId)
    {
        return new ConsoleInputStream(eventBroker, applicationId, true);
    }

    public static ConsoleInputStream attachStderr(CEPEngine eventBroker, UUID applicationId)
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
        query.close();
        super.close();
    }

    private class EventReceiver implements QueryListener
    {
        @Override
        public void receive(long timestamp, Event[] inEvents, Event[] outEvents)
        {
            for (Event event : inEvents)
            {
                String text = (String) event.getField(2);
                buffer.write(text.getBytes());
            }
        }
    }
}
