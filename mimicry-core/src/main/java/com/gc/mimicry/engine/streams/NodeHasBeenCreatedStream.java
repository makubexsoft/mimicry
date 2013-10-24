package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.cep.StreamDescription;
import com.gc.mimicry.cep.Type;

public class NodeHasBeenCreatedStream
{
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("NodeHasBeenCreated");
        DESCRIPTION.addField("timestamp", Type.LONG);
        DESCRIPTION.addField("nodeId", Type.STRING);
        DESCRIPTION.addField("osVersion", Type.STRING);
        DESCRIPTION.addField("architecture", Type.STRING);
        DESCRIPTION.addField("javaVersion", Type.STRING);
        DESCRIPTION.addField("numberCores", Type.INT);
        DESCRIPTION.addField("operatingSystem", Type.STRING);
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
