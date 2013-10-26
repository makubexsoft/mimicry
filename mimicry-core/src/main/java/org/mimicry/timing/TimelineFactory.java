package org.mimicry.timing;

public class TimelineFactory
{
    private TimelineFactory()
    {
    }

    public static TimelineFactory getDefault()
    {
        return new TimelineFactory();
    }

    public Timeline createTimeline(TimelineType type, long initialMillis)
    {
        switch (type)
        {
            case REALTIME:
                return new RealtimeClock(initialMillis);

            case DISCRETE:
                return new DiscreteClock(initialMillis);

            case SYSTEM:
                return new SystemClock();

            default:
                throw new UnsupportedOperationException("Unsupported time line type: " + type);
        }
    }
}
