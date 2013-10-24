package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Stream;

public class ApplicationEventStream
{
    public static final String NAME = "ApplicationEvent";
    private static final String STREAM_FORMAT = "define stream " + NAME
            + " ( applicationId string, controlFlowId string, direction string, eventAsJson string )";

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