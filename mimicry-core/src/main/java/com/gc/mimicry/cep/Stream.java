package com.gc.mimicry.cep;

public interface Stream
{
    public void send(Object... values);

    public String getName();

    public void addStreamListener(StreamListener l);

    public void removeStreamListener(StreamListener l);
}