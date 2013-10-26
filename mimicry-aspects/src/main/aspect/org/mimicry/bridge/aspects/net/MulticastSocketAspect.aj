package org.mimicry.bridge.aspects.net;

import java.io.IOException;
import java.lang.reflect.Constructor;

import java.net.MulticastSocket;

import org.mimicry.bridge.aspects.AspectUtils;
import org.mimicry.bridge.net.ManagedMulticastSocket;

/**
 * This aspect replaces the {@link MulticastSocket} by the
 * {@link ManagedMulticastSocket} stub.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect MulticastSocketAspect
{
	declare	parents : ((MulticastSocket+) && !(MulticastSocket)) extends ManagedMulticastSocket;

	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut multicastSocketCtor() : 
					call(MulticastSocket.new(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*);

	public pointcut multicastSocketReflectClass( @SuppressWarnings("rawtypes") Class c ) : 
					call(Object java.lang.Class.newInstance()) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) && 
					if(c.getName().equals("java.net.MulticastSocket"));

	public pointcut multicastSocketReflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
					call(Object Constructor.newInstance(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) &&
					if(c.getDeclaringClass().getName().equals("java.net.MulticastSocket"));

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : multicastSocketCtor()
	{
		return AspectUtils.invokeConstructor( ManagedMulticastSocket.class, thisJoinPoint.getArgs() );
	}

	@SuppressWarnings("rawtypes")
	Object around( Class c ) throws IOException : multicastSocketReflectClass(c)  
	{
		return new ManagedMulticastSocket();
	}

	@SuppressWarnings("rawtypes")
	Object around( Constructor c ) : multicastSocketReflectCtor(c)  
	{
		return AspectUtils.invokeReflectiveConstructor( ManagedMulticastSocket.class, thisJoinPoint.getArgs() );
	}
}
