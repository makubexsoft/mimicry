package com.gc.mimicry.core.messaging;

import java.io.Closeable;

public interface Publisher extends Closeable
{

    public Topic getTopic();

    public void send(Message msg);

    public void close();
}
