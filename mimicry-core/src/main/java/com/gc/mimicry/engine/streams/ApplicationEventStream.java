package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.cep.StreamDescription;
import com.gc.mimicry.cep.Type;

public class ApplicationEventStream
{
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("ApplicationEvent");
        DESCRIPTION.addField("applicationId", Type.STRING);
        DESCRIPTION.addField("controlFlowId", Type.STRING);
        DESCRIPTION.addField("direction", Type.STRING);
        DESCRIPTION.addField("eventAsJson", Type.STRING);
    }

    public static Stream get(CEPEngine eventEngine)
    {
        Stream stream = eventEngine.getStream(DESCRIPTION.getName());
        if (stream != null)
        {
            return stream;
        }
        return eventEngine.defineStream(DESCRIPTION);
    }
}