package com.gc.mimicry.bridge.aspects.net;

import java.lang.reflect.Constructor;
import java.net.Socket;

import com.gc.mimicry.bridge.aspects.AspectUtils;
import com.gc.mimicry.bridge.net.ManagedSocket;

/**
 * This aspect replaces the {@link Socket} class by the {@link ManagedSocket}
 * stub.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect SocketAspect
{
	declare	parents : ((Socket+) && !(Socket)) extends ManagedSocket;

	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut socketCtor() : 
					call(Socket.new(..)) && 
					!within(com.gc.mimicry..*) && 
					!within(java..*);

	public pointcut socketReflectClass( @SuppressWarnings("rawtypes") Class c ) : 
					call(Object java.lang.Class.newInstance()) && 
					!within(com.gc.mimicry..*) && 
					!within(java..*) && 
					target(c) && 
					if(c.getName().equals("java.net.Socket"));

	public pointcut socketReflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
					call(Object Constructor.newInstance(..)) && 
					!within(com.gc.mimicry..*) && 
					!within(java..*) && 
					target(c) &&
					if(c.getDeclaringClass().getName().equals("java.net.Socket"));

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : socketCtor()
	{
		return AspectUtils.invokeConstructor( ManagedSocket.class, thisJoinPoint.getArgs() );
	}

	@SuppressWarnings("rawtypes")
	Object around( Class c ) : socketReflectClass(c)  
	{
		return new ManagedSocket();
	}

	@SuppressWarnings("rawtypes")
	Object around( Constructor c ) : socketReflectCtor(c)  
	{
		return AspectUtils.invokeReflectiveConstructor( ManagedSocket.class, thisJoinPoint.getArgs() );
	}
}
