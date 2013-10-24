package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;
import com.gc.mimicry.cep.StreamDescription;
import com.gc.mimicry.cep.Type;

public class NodeRemovedStream
{
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("NodeRemoved");
        DESCRIPTION.addField("timestamp", Type.LONG);
        DESCRIPTION.addField("nodeId", Type.STRING);
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
