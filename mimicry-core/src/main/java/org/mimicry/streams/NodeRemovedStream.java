package org.mimicry.streams;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamDescription;
import org.mimicry.cep.Type;

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
