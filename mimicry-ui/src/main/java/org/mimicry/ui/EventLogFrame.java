package org.mimicry.ui;

import java.awt.BorderLayout;

import org.mimicry.Simulation;

import com.jidesoft.docking.DockableFrame;

public class EventLogFrame extends DockableFrame
{
	public EventLogFrame(Simulation simulation)
	{
		setTitle("Event Log");
		setLayout( new BorderLayout() );
		
		add( new EventLogView( simulation.getEventEngine() ) , BorderLayout.CENTER);
	}
}
