package org.mimicry.bridge.aspects.net;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;

import org.mimicry.bridge.aspects.AspectUtils;
import org.mimicry.bridge.net.ManagedServerSocket;


/**
 * This aspect replaces the {@link ServerSocket} by the
 * {@link ManagedServerSocket} stub.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect ServerSocketAspect
{
	declare	parents : ((ServerSocket+) && !(ServerSocket)) extends ManagedServerSocket;

	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut serverSocketCtor() : 
					call(ServerSocket.new(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*);

	public pointcut serverSocketReflectClass( @SuppressWarnings("rawtypes") Class c ) : 
					call(Object java.lang.Class.newInstance()) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) && 
					if(c.getName().equals("java.net.ServerSocket"));

	public pointcut serverSocketReflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
					call(Object Constructor.newInstance(..)) && 
					!within(org.mimicry..*) && 
					!within(java..*) && 
					target(c) &&
					if(c.getDeclaringClass().getName().equals("java.net.ServerSocket"));

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : serverSocketCtor()
	{
		return AspectUtils.invokeConstructor( ManagedServerSocket.class, thisJoinPoint.getArgs() );
	}

	@SuppressWarnings("rawtypes")
	Object around( Class c ) throws IOException : serverSocketReflectClass(c)  
	{
		return new ManagedServerSocket();
	}

	@SuppressWarnings("rawtypes")
	Object around( Constructor c ) : serverSocketReflectCtor(c)  
	{
		return AspectUtils.invokeReflectiveConstructor( ManagedServerSocket.class, thisJoinPoint.getArgs() );
	}
}
