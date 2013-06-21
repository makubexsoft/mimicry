package com.gc.mimicry.bridge.aspects;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.gc.mimicry.bridge.SimulatorBridge;

/**
 * This aspect prevents the simulated application to shutdown the VM.
 * 
 * @author Marc-Christian Schulze
 * 
 */
public aspect LifecycleAspect
{
	// ------------------------------------------------------------------------------------------
	// Pointcuts
	// ------------------------------------------------------------------------------------------
	public pointcut systemExit( int status ) : 
					call(void System.exit(int)) && 
					args(status);

	public pointcut runtimeExit( int status ) : 
					call(void Runtime.exit(int)) && 
					args(status);

	public pointcut runtimeHalt( int status ) : 
					call(void Runtime.halt(int)) && 
					args(status);

	public pointcut newFrame( JFrame frame ) : 
					call(JFrame+.new(..)) && 
					target(frame);

	public pointcut disposeFrame( JFrame frame ) : 
					call(void JFrame+.dispose()) && 
					target(frame);

	// ------------------------------------------------------------------------------------------
	// Advices
	// ------------------------------------------------------------------------------------------
	void around( int status ) : systemExit(status)
	{
		SimulatorBridge.shutdownApplication();
	}

	void around( JFrame frame ) : disposeFrame(frame) 
	{
		if ( frame.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE )
		{
			SimulatorBridge.shutdownApplication();
		}
		else
		{
			proceed( null );
		}
	}

	after( final JFrame frame ) : newFrame(frame) 
	{
		frame.addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( WindowEvent e )
			{
				if ( frame.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE )
				{
					SimulatorBridge.shutdownApplication();
				}
			}
		} );
	}
}
