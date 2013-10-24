package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.cep.StreamDescription;
import com.gc.mimicry.cep.Type;

public class StdOutStream
{
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("StdOut");
        DESCRIPTION.addField("timestamp", Type.LONG);
        DESCRIPTION.addField("appId", Type.STRING);
        DESCRIPTION.addField("text", Type.STRING);
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