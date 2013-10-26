package org.mimicry.junit;

import org.mimicry.timing.TimelineType;

public @interface SimulationConfiguration
{
	TimelineType timeline() default TimelineType.SYSTEM;

	long startTimeInMillis() default 0;

	double multiplier() default 1.0;
}
