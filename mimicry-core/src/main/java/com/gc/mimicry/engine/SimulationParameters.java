package com.gc.mimicry.engine;

import java.io.Serializable;

import com.gc.mimicry.engine.timing.TimelineType;

public class SimulationParameters implements Serializable
{
    private static final long serialVersionUID = 5041761374652137059L;
    private TimelineType timelineType = TimelineType.REALTIME;
    private long initialTimeMillis = 0;

    public TimelineType getTimelineType()
    {
        return timelineType;
    }

    public void setTimelineType(TimelineType timelineType)
    {
        this.timelineType = timelineType;
    }

    public long getInitialTimeMillis()
    {
        return initialTimeMillis;
    }

    public void setInitialTimeMillis(long initialTimeMillis)
    {
        this.initialTimeMillis = initialTimeMillis;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("NetworkConfiguration [timelineType=");
        builder.append(timelineType);
        builder.append(", initialTimeMillis=");
        builder.append(initialTimeMillis);
        builder.append("]");
        return builder.toString();
    }
}
