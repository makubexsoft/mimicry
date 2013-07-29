package com.gc.mimicry.engine;

import com.gc.mimicry.engine.timing.ClockType;

public class NetworkConfiguration
{
    private ClockType clockType = ClockType.REALTIME;
    private long initialTimeMillis = 0;

    public ClockType getClockType()
    {
        return clockType;
    }

    public void setClockType(ClockType clockType)
    {
        this.clockType = clockType;
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
        builder.append("NetworkConfiguration [clockType=");
        builder.append(clockType);
        builder.append(", initialTimeMillis=");
        builder.append(initialTimeMillis);
        builder.append("]");
        return builder.toString();
    }
}
