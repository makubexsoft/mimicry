package org.mimicry.bridge.aspects;

import java.io.IOException;

import org.mimicry.bridge.SimulatorBridge;

public aspect ClassLoadingAspect 
{
	pointcut getSystemClassLoader()  :
		call(ClassLoader ClassLoader+.getSystemClassLoader());
	
	pointcut getSystemResourceAsStream(String name) :
		call(java.io.InputStream ClassLoader.getSystemResourceAsStream(String)) &&
		args(name);
	
	pointcut getSystemResource(String name) :
		call(java.net.URL ClassLoader+.getSystemResource(String)) &&
		args(name);
	
	pointcut getSystemResources(String name) :
		call(java.util.Enumeration<java.net.URL> ClassLoader+.getSystemResources(String) ) &&
		args(name);
	
	Object around() : getSystemClassLoader()
	{
		return SimulatorBridge.getSystemClassLoader();
	}
	
	Object around(String name) throws IOException : getSystemResources(name)
	{
		return SimulatorBridge.getSystemClassLoader().getResources(name);
	}
	
	Object around(String name) : getSystemResourceAsStream(name)
	{
		return SimulatorBridge.getSystemClassLoader().getResourceAsStream( name );
	}
	
	Object around(String name) : getSystemResource(name)
	{
		return SimulatorBridge.getSystemClassLoader().getResource( name );
	}
}