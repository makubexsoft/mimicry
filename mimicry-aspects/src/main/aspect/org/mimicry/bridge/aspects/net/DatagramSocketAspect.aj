package org.mimicry.bridge.aspects.net;

import java.lang.reflect.Constructor;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.mimicry.bridge.aspects.AspectUtils;
import org.mimicry.bridge.net.ManagedDatagramSocket;


/**
 * This aspect replaces the DatagramSocket with the
 * {@link ManagedDatagramSocket} stub.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect DatagramSocketAspect
{
	declare	parents : ((DatagramSocket+) && !(DatagramSocket) && !(MulticastSocket+)) extends ManagedDatagramSocket;

	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut datagramSocketCtor() : 
					call(DatagramSocket.new(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*);

	public pointcut datagramSocketReflectClass( @SuppressWarnings("rawtypes") Class c ) : 
					call(Object java.lang.Class.newInstance()) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) && 
					if(c.getName().equals("java.net.DatagramSocket"));

	public pointcut datagramSocketReflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
					call(Object Constructor.newInstance(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) &&
					if(c.getDeclaringClass().getName().equals("java.net.DatagramSocket"));

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : datagramSocketCtor()
	{
		return AspectUtils.invokeConstructor( ManagedDatagramSocket.class, thisJoinPoint.getArgs() );
	}

	@SuppressWarnings("rawtypes")
	Object around( Class c ) throws SocketException : datagramSocketReflectClass(c)  
	{
		return new ManagedDatagramSocket();
	}

	@SuppressWarnings("rawtypes")
	Object around( Constructor c ) : datagramSocketReflectCtor(c)  
	{
		return AspectUtils.invokeReflectiveConstructor( ManagedDatagramSocket.class, thisJoinPoint.getArgs() );
	}
}
