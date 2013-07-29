package com.gc.mimicry.ui;

import java.awt.BorderLayout;

import com.gc.mimicry.engine.Simulation;
import com.jidesoft.docking.DockableFrame;

public class EventLogFrame extends DockableFrame
{
	public EventLogFrame(Simulation simulation)
	{
		setTitle("Event Log");
		setLayout( new BorderLayout() );
		
		add( new EventLogView( simulation.getEventBroker() ) , BorderLayout.CENTER);
	}
}
