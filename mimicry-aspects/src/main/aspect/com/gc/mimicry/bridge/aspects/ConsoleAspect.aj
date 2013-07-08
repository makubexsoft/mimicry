package com.gc.mimicry.bridge.aspects;

import com.gc.mimicry.bridge.SimulatorBridge;

public aspect ConsoleAspect
{
	pointcut getSystemIn() : get(java.io.InputStream System.in) && !within(com.gc.mimicry..*);
	pointcut setSystemIn(java.io.InputStream stream) : set(java.io.InputStream System.in) && !within(com.gc.mimicry..*) && args(stream);
	
	Object around() : getSystemIn()
	{
		return SimulatorBridge.getSystemInputStream();
	}
	
	void around(java.io.InputStream stream) : setSystemIn(stream)
	{
		SimulatorBridge.setSystemInputStream(stream);
	}
}