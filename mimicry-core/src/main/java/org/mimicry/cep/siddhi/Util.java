package org.mimicry.cep.siddhi;

public class Util
{
    public static org.mimicry.cep.Event[] wrap(org.wso2.siddhi.core.event.Event[] events)
    {
        if (events == null)
        {
            return new org.mimicry.cep.Event[0];
        }
        org.mimicry.cep.Event[] wrappedEvents = new org.mimicry.cep.Event[events.length];
        int i = 0;
        for (org.wso2.siddhi.core.event.Event event : events)
        {
            wrappedEvents[i] = new SiddhiEvent(event);
            i++;
        }
        return wrappedEvents;
    }
}
