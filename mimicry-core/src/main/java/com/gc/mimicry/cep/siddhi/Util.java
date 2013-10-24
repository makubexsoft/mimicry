package com.gc.mimicry.cep.siddhi;

public class Util
{
    public static com.gc.mimicry.cep.Event[] wrap(org.wso2.siddhi.core.event.Event[] events)
    {
        com.gc.mimicry.cep.Event[] wrappedEvents = new com.gc.mimicry.cep.Event[events.length];
        int i = 0;
        for (org.wso2.siddhi.core.event.Event event : events)
        {
            wrappedEvents[i] = new SiddhiEvent(event);
            i++;
        }
        return wrappedEvents;
    }
}
