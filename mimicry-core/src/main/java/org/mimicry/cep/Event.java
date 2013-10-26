package org.mimicry.cep;

public interface Event
{
    public long getTimeStamp();

    public Object getField(int index);
}
