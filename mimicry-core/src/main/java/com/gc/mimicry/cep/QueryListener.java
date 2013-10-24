package com.gc.mimicry.cep;

public interface QueryListener
{
    public void receive(long timestamp, Event[] inEvents, Event[] outEvents);
}
