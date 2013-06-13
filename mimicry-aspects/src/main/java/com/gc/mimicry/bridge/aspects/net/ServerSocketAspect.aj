package com.gc.mimicry.bridge.aspects.net;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;

import com.gc.mimicry.bridge.aspects.AspectUtils;
import com.gc.mimicry.bridge.net.ManagedServerSocket;

public aspect ServerSocketAspect 
{
	declare	parents : ((ServerSocket+) && !(ServerSocket)) extends ManagedServerSocket;

	//------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut serverSocketCtor() : 
					call(ServerSocket.new(..)) && 
					!within(com.gc.mimicry..*) && 
					!within(java..*);
	
	public pointcut serverSocketReflectClass( @SuppressWarnings("rawtypes") Class c ) : 
					call(Object java.lang.Class.newInstance()) && 
					!within(com.gc.mimicry..*) && 
					!within(java..*) && 
					target(c) && 
					if(c.getName().equals("java.net.ServerSocket"));
	
	public pointcut serverSocketReflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
					call(Object Constructor.newInstance(..)) && 
					!within(com.gc.mimicry..*) && 
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
