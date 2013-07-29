package com.gc.mimicry.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.gc.mimicry.engine.Simulation;
import com.gc.mimicry.ui.groovy.ConsoleFrame;
import com.jidesoft.docking.DefaultDockableHolder;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import com.jidesoft.docking.DockingManager;
import com.jidesoft.docking.DockingManagerGroup;

public class MainFrame extends DefaultDockableHolder
{
	private static final long	serialVersionUID	= -1396222277201712462L;
	private DockingManagerGroup mgrGroup;
	
	public MainFrame()
	{
		setTitle("The Mimicry Framework");
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
//		this.mgrGroup = new DockingManagerGroup();
//		mgrGroup.add( getDockingManager() );
		
		configureDockingManager();
		
		createSimulationAndFrames();
		
		setJMenuBar( createMenuBar() );
		
		setSize( 800, 600 );
		setVisible( true );
	}

	public MainFrame(DockingManagerGroup mgrGroup)
	{
		setTitle("The Mimicry Framework");
		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		
//		this.mgrGroup = mgrGroup;
//		mgrGroup.add( getDockingManager() );
		
		configureDockingManager();
		setJMenuBar( createMenuBar() );
		
		setSize( 800, 600 );
		setVisible( true );
	}
	
	private void configureDockingManager()
	{
		getDockingManager().setInitSplitPriority( DockingManager.SPLIT_EAST_WEST_SOUTH_NORTH );
		getDockingManager().setCrossDraggingAllowed( true );
		getDockingManager().setCrossDroppingAllowed( true );
		getDockingManager().setEasyTabDock( true );
		getDockingManager().getWorkspace().setAcceptDockableFrame( true );
		getDockingManager().getWorkspace().setLayout( new BorderLayout() );
	}

	private void createSimulationAndFrames()
	{
		try
		{
			Simulation simulation = SimulationFactory.getDefault().createSimulation();
			createFrames( simulation );
		}
		catch ( IOException e )
		{
			JOptionPane.showMessageDialog( this, "Failed to create a simulation.\nReason: " + e.getMessage(), "Simulation failed.", JOptionPane.ERROR_MESSAGE );
		}
	}

	private void createFrames( Simulation simulation )
	{
		DockableFrame eventLogFrame = new EventLogFrame( simulation );
		eventLogFrame.setKey( "mimicry.eventLog" );
		eventLogFrame.getContext().setInitMode( DockContext.STATE_FRAMEDOCKED );
		eventLogFrame.getContext().setInitSide( DockContext.DOCK_SIDE_SOUTH );

		DockableFrame consoleFrame = new ConsoleFrame( simulation );
		consoleFrame.setKey( "mimicry.consoleFrame" );
		consoleFrame.getContext().setInitMode( DockContext.STATE_FRAMEDOCKED );
		consoleFrame.getContext().setInitSide( DockContext.DOCK_SIDE_CENTER );
		
		NetworkBrowserFrame networkBrowser = new NetworkBrowserFrame();
		networkBrowser.setKey( "mimicry.networkBrowser" );
		networkBrowser.getContext().setInitMode( DockContext.STATE_FRAMEDOCKED );
		networkBrowser.getContext().setInitSide( DockContext.DOCK_SIDE_WEST );

		getDockingManager().beginLoadLayoutData();
		getDockingManager().addFrame( consoleFrame );
		getDockingManager().addFrame( eventLogFrame );
		getDockingManager().addFrame( networkBrowser );
		getDockingManager().loadLayoutData();
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu menuFile = new JMenu( "File" );
		JMenuItem itmExit = new JMenuItem( "Exit" );
		
		JMenu menuView = new JMenu("View");
		//JMenuItem itmNewWindow = new JMenuItem("Open new window");

		JMenu menuHelp = new JMenu( "Help" );
		JMenuItem itmManual = new JMenuItem("Show Manual");
		JMenuItem itmAbout = new JMenuItem("About Mimicry ...");

		menuBar.add( menuFile );
		menuFile.add( itmExit );
		
		menuBar.add(menuView);
		//menuView.add(itmNewWindow);

		menuBar.add( menuHelp );
		menuHelp.add(itmManual);
		menuHelp.add(itmAbout);
		
//		itmNewWindow.addActionListener( new ActionListener()
//		{
//			
//			@Override
//			public void actionPerformed( ActionEvent e )
//			{
//				new MainFrame(mgrGroup);
//			}
//		} );
		itmAbout.addActionListener( new ActionListener()
		{
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				AboutDialog dlg = new AboutDialog();
				UIUtils.centerOnScreen( dlg );
				dlg.setModal( true );
				dlg.setVisible( true );
			}
		} );
		itmManual.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if (Desktop.isDesktopSupported()) {
				    try {
				        File myFile = new File("Mimicry.pdf");
				        Desktop.getDesktop().open(myFile);
				    } catch (IOException ex) {
				        // no application registered for PDFs
				    	JOptionPane.showMessageDialog( MainFrame.this, "Can't open PDF file. Opening containing folder instead." );
				    	try
						{
							Desktop.getDesktop().open( new File(".") );
						}
						catch ( IOException e1 )
						{
							// should not happen
						}
				    }
				}
			}
		} );

		return menuBar;
	}

}
