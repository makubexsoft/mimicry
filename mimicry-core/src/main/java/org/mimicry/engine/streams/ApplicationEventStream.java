package org.mimicry.engine.streams;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamDescription;
import org.mimicry.cep.Type;

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