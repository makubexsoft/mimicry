package com.gc.mimicry.bridge.aspects;

import java.util.Calendar;
import java.util.Date;

import com.gc.mimicry.bridge.SimulatorBridge;
import com.gc.mimicry.bridge.threading.ManagedThread;
import com.gc.mimicry.bridge.threading.ThreadScheduler;

/**
 * This aspect controls the time line of the simulated application.
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
		SimulatorBridge.getClock().sleepFor( millis );
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
