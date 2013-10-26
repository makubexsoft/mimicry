package org.mimicry.engine.streams;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamDescription;
import org.mimicry.cep.Type;

public class StdInStream
{
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("StdIn");
        DESCRIPTION.addField("timestamp", Type.LONG);
        DESCRIPTION.addField("appId", Type.STRING);
        DESCRIPTION.addField("base64Input", Type.STRING);
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