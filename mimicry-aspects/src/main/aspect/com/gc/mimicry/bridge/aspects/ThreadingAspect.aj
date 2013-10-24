package com.gc.mimicry.bridge.aspects;

import java.lang.reflect.Constructor;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.net.ManagedSocket;
import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.bridge.threading.MonitorInterceptor;

/**
 * This aspect is part of the life cycle management of the simulated application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
privileged aspect ThreadingAspect
{
	declare	parents : ((Thread+) && !(Thread)) extends ManagedThread;

	public pointcut ctor() : 
		call(java.lang.Thread.new(..)) && 
		!within(com.gc.mimicry..*) && 
		!within(java.lang..*);

	public pointcut reflectCls( @SuppressWarnings("rawtypes") Class c ) : 
		call(Object java.lang.Class.newInstance()) && 
		!within(com.gc.mimicry..*) && 
		!within(java..*) && 
		target(c) && 
		if(c.getName().equals("java.lang.Thread"));

	public pointcut reflectCtor( @SuppressWarnings("rawtypes") Constructor c ) : 
		call(Object Constructor.newInstance(..)) && 
		!within(com.gc.mimicry..*) && 
		!within(java..*) && 
		target(c) &&
		if(c.getDeclaringClass().getName().equals("java.lang.Thread"));

	public pointcut run( ManagedThread t ) :
		execution(void ManagedThread+.run()) &&
		!within(com.gc.mimicry..*) && 
		this(t);
	
	public pointcut waitP( Object target, long millis ) : 
		!within(com.gc.mimicry..*) && 
		call(void *.wait(long)) && 
		target(target) && 
		args(millis);

public pointcut waitP2( Object target ) : 
		!within(com.gc.mimicry..*) && 
		call(void *.wait()) && 
		target(target);

public pointcut notifyP( Object target ) :
		!within(com.gc.mimicry..*) && 
		call(void *.notify()) && 
		target(target);

public pointcut notifyAllP( Object target ) :
		!within(com.gc.mimicry..*) && 
		call(void *.notifyAll()) && 
		target(target);

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	/**
	 * Re-throw {@link ThreadShouldTerminateException}s in order to avoid that
	 * the user code can suppress it.
	 */
	before( Throwable t ): handler(Exception+) && args(t) && !within(com.gc.mimicry..*)
	{
		if ( t instanceof ThreadDeath )
		{
			throw (ThreadDeath) t;
		}
	}

	//
	// Thread Termination
	//
	after( ManagedThread t ) : run(t)
	{
		SimulatorBridge.getThreadManager().threadTerminated( t );
	}

	after( ManagedThread t) throwing(Throwable th ) : run(t)
	{
		SimulatorBridge.getThreadManager().threadTerminated( t, th );
	}
	
	void around( Object target, long millis ) throws InterruptedException : waitP(target, millis)  
	{
		MonitorInterceptor.monitorWait( target, millis );
	}

	void around( Object target ) throws InterruptedException : waitP2(target)  
	{
		MonitorInterceptor.monitorWait( target );
	}

	void around( Object target ) : notifyP(target) 
	{
		MonitorInterceptor.monitorNotify( target );
	}

	void around( Object target ) : notifyAllP(target)  
	{
		MonitorInterceptor.monitorNotifyAll( target );
	}
	
	after(Thread t) : !within(com.gc.mimicry..*) && call(void Thread+.start()) && target(t)
	{
		SimulatorBridge.getThreadManager().getScheduler().threadStarted((ManagedThread)t);
	}

	//
	// Thread constructions
	//
	Object around() : ctor()  
	{
		return AspectUtils.invokeConstructor( ManagedThread.class, thisJoinPoint.getArgs() );
	}

	@SuppressWarnings("rawtypes")
	Object around( Class c ) : reflectCls(c)  
	{
		return new ManagedThread();
	}

	@SuppressWarnings("rawtypes")
	Object around( Constructor c ) : reflectCtor(c)  
	{
		return AspectUtils.invokeReflectiveConstructor( ManagedThread.class, thisJoinPoint.getArgs() );
	}
}
