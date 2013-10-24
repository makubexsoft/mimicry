package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;

public class NodeHasBeenCreatedStream
{
    public static final String NAME = "NodeHasBeenCreated";
    private static final String STREAM_FORMAT = "define stream "
            + NAME
            + " ( timestamp long, nodeId string, osVersion string, architecture string, javaVersion string, numberCores int, operatingSystem string )";

    public static Stream get(CEPEngine eventEngine)
    {
        Stream stream = eventEngine.getStream(NAME);
        if (stream != null)
        {
            return stream;
        }
        return eventEngine.defineStream(STREAM_FORMAT);
    }
}
