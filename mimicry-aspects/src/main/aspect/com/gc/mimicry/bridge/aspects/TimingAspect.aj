package com.gc.mimicry.bridge.aspects;

import java.util.Calendar;
import java.util.Date;

import com.gc.mimicry.bridge.SimulatorBridge;

/**
 * This aspect controlls the timeline of the simulated application.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect TimingAspect
{
	private static final long	E6	= 1000 * 1000;

	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut currentMillis() : 
					!within(com.gc.mimicry..*) && 
					call(long System.currentTimeMillis());

	public pointcut nanoTime() : 
					!within(com.gc.mimicry..*) && 
					call(long System.nanoTime());

	public pointcut sleep( long millis ) : 
					!within(com.gc.mimicry..*) && 
					call(void Thread.sleep(long)) && 
					args(millis);

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

	public pointcut newCalendar( Calendar cal ) : 
					!within(com.gc.mimicry..*) && 
					call(Calendar+.new(..)) && 
					target(cal);

	public pointcut calendargetInstance() : 
					!within(com.gc.mimicry..*) && 
					call(Calendar Calendar.getInstance(..));

	public pointcut newDate() : 
					!within(com.gc.mimicry..*) && 
					call(Date.new());

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : currentMillis()  
	{
		return SimulatorBridge.getClock().currentMillis();
	}

	Object around() : nanoTime()  
	{
		return SimulatorBridge.getClock().currentMillis() * E6;
	}

	void around( long millis ) throws InterruptedException : sleep(millis)  
	{
		SimulatorBridge.getClock().sleepUntil( SimulatorBridge.getClock().currentMillis() + millis );
	}

	void around( Object target, long millis ) throws InterruptedException : waitP(target, millis)  
	{
		SimulatorBridge.getClock().waitOnUntil( target, SimulatorBridge.getClock().currentMillis() + millis );
	}

	void around( Object target ) throws InterruptedException : waitP2(target)  
	{
		SimulatorBridge.getClock().waitOn( target );
	}

	void around( Object target ) : notifyP(target) 
	{
		SimulatorBridge.getClock().notifyOnTarget( target );
	}

	void around( Object target ) : notifyAllP(target)  
	{
		SimulatorBridge.getClock().notifyAllOnTarget( target );
	}

	after( Calendar cal ) : newCalendar(cal) {
		cal.setTimeInMillis( SimulatorBridge.getClock().currentMillis() );
	}

	after() returning (Object o ): calendargetInstance() 
	{
		((Calendar) o).setTimeInMillis( SimulatorBridge.getClock().currentMillis() );
	}

	Object around() : newDate() 
	{
		return new Date( SimulatorBridge.getClock().currentMillis() );
	}
}
