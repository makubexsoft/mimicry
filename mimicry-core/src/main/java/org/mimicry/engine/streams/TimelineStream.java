package org.mimicry.engine.streams;

import org.mimicry.cep.CEPEngine;
import org.mimicry.cep.Event;
import org.mimicry.cep.Stream;
import org.mimicry.cep.StreamDescription;
import org.mimicry.cep.Type;

public class TimelineStream
{
    public static final String COMMAND_START = "start";
    public static final String COMMAND_STOP = "stop";
    public static final String COMMAND_ADVANCE = "advance";
    public static StreamDescription DESCRIPTION;
    static
    {
        DESCRIPTION = new StreamDescription("Timeline");
        DESCRIPTION.addField("command", Type.STRING);
        DESCRIPTION.addField("multiplier", Type.FLOAT);
        DESCRIPTION.addField("deltaT", Type.LONG);
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
}