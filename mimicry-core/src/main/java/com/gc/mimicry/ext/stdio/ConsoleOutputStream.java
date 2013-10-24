package com.gc.mimicry.ext.stdio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.engine.streams.StdInStream;
import com.google.common.base.Preconditions;

public class ConsoleOutputStream extends OutputStream
{
    private final UUID applicationId;
    private final Stream stream;

    private ConsoleOutputStream(CEPEngine eventEngine, UUID applicationId)
    {
        Preconditions.checkNotNull(eventEngine);
        Preconditions.checkNotNull(applicationId);

        this.applicationId = applicationId;

        stream = StdInStream.get(eventEngine);
    }

    public static ConsoleOutputStream attachStdin(CEPEngine eventEngine, UUID applicationId)
    {
        return new ConsoleOutputStream(eventEngine, applicationId);
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
        // TODO: do we need a concrete time stamp at this point?
        stream.send(0, applicationId, data);
    }
}
