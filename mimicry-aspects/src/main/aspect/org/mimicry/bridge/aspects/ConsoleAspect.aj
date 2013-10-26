package org.mimicry.bridge.aspects;

import org.mimicry.bridge.SimulatorBridge;

public aspect ConsoleAspect
{
	pointcut getSystemIn() : get(java.io.InputStream System.in) && !within(org.mimicry..*);
	pointcut setSystemIn(java.io.InputStream stream) : set(java.io.InputStream System.in) && !within(org.mimicry..*) && args(stream);
	
	Object around() : getSystemIn()
	{
		return SimulatorBridge.getSystemInputStream();
	}
	
	void around(java.io.InputStream stream) : setSystemIn(stream)
	{
		SimulatorBridge.setSystemInputStream(stream);
	}
}