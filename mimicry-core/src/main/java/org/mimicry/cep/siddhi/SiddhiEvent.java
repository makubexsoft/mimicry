package org.mimicry.cep.siddhi;

import org.mimicry.cep.Event;

import com.google.common.base.Preconditions;

public class SiddhiEvent implements Event
{
    private final org.wso2.siddhi.core.event.Event event;

    public SiddhiEvent(org.wso2.siddhi.core.event.Event e)
    {
        Preconditions.checkNotNull(e);
        event = e;
    }

    @Override
    public long getTimeStamp()
    {
        return event.getTimeStamp();
    }

    @Override
    public Object getField(int index)
    {
        return event.getData(index);
    }
}
