package com.gc.mimicry.core.runtime;

import com.gc.mimicry.core.timing.ClockType;

public class NetworkConfiguration
{
    private ClockType clockType;
    private long initialTimeMillis;

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
        return "NetworkConfiguration [clockType=" + clockType + "]";
    }

}
