package org.mimicry.junit;

public @interface EngineConfiguration
{
	String workspace() default "/tmp/mimicry";
}
