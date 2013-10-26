package org.mimicry.bridge.aspects;

import java.util.Calendar;
import java.util.Date;

import org.mimicry.bridge.SimulatorBridge;
import org.mimicry.bridge.threading.ManagedThread;
import org.mimicry.bridge.threading.ThreadScheduler;

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
					!within(org.mimicry..*) && 
					call(long System.currentTimeMillis());

	public pointcut nanoTime() : 
					!within(org.mimicry..*) && 
					call(long System.nanoTime());

	public pointcut sleep( long millis ) : 
					!within(org.mimicry..*) && 
					call(void Thread.sleep(long)) && 
					args(millis);

	

	public pointcut newCalendar( Calendar cal ) : 
					!within(org.mimicry..*) && 
					call(Calendar+.new(..)) && 
					target(cal);

	public pointcut calendargetInstance() : 
					!within(org.mimicry..*) && 
					call(Calendar Calendar.getInstance(..));

	public pointcut newDate() : 
					!within(org.mimicry..*) && 
					call(Date.new());

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	Object around() : currentMillis()  
	{
		return SimulatorBridge.getTimeline().currentMillis();
	}

	Object around() : nanoTime()  
	{
		return SimulatorBridge.getTimeline().currentMillis() * E6;
	}

	void around( long millis ) throws InterruptedException : sleep(millis)  
	{
		SimulatorBridge.getTimeline().sleepFor( millis );
	}

	after( Calendar cal ) : newCalendar(cal) {
		cal.setTimeInMillis( SimulatorBridge.getTimeline().currentMillis() );
	}

	after() returning (Object o ): calendargetInstance() 
	{
		((Calendar) o).setTimeInMillis( SimulatorBridge.getTimeline().currentMillis() );
	}

	Object around() : newDate() 
	{
		return new Date( SimulatorBridge.getTimeline().currentMillis() );
	}
}
