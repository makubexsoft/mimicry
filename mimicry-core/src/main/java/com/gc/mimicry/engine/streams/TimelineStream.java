package com.gc.mimicry.engine.streams;

import com.gc.mimicry.cep.CEPEngine;
import com.gc.mimicry.cep.Event;
import com.gc.mimicry.cep.Stream;

public class TimelineStream
{
    public static final String NAME = "Timeline";
    public static final String COMMAND_START = "start";
    public static final String COMMAND_STOP = "stop";
    public static final String COMMAND_ADVANCE = "advance";
    private static final String STREAM_FORMAT = "define stream " + NAME
            + " ( command string, multiplier float, deltaT long )";

    public static String getCommand(Event evt)
    {
        return (String) evt.getField(0);
    }

    public static double getMultiplier(Event evt)
    {
        return (Double) evt.getField(1);
    }

    public static long getDeltaT(Event evt)
    {
        return (Long) evt.getField(2);
    }

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