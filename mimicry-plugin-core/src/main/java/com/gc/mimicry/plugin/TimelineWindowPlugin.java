package com.gc.mimicry.plugin;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.gc.mimicry.core.runtime.SimulatedNetwork;
import com.gc.mimicry.core.timing.Clock;
import com.gc.mimicry.core.timing.ClockType;
import com.gc.mimicry.core.timing.DiscreteClock;
import com.gc.mimicry.core.timing.RealtimeClock;
import com.gc.mimicry.core.timing.net.ClockController;
import com.gc.mimicry.core.timing.net.ClockDriver;

public class TimelineWindowPlugin extends JDialog
{
	private boolean					clockRunning;
	private Clock					localClock;
	private final ClockDriver		clockDriver;
	private final Thread			updateThread;

	private final SimulatedNetwork	network;
	private final ClockController	clockCtrl;
	private JTextField				txtMultiplier;
	private JTextField				txtDeltaT;
	private JPanel					panRealtime;
	private JPanel					panDiscrete;
	private JButton					btnStart;
	private JButton					btnAdvance;
	private JLabel					lblCurrentTime;
	private JTabbedPane				tabbedPane;

	public TimelineWindowPlugin(SimulatedNetwork network, ClockController clockCtrl)
	{
		this.network = network;
		this.clockCtrl = clockCtrl;
		createUIComponents();
		long initialMillis = network.getConfig().getInitialTimeMillis();

		if ( network.getConfig().getClockType() == ClockType.REALTIME )
		{
			localClock = new RealtimeClock( initialMillis );
			setupRealtimeBehaviour();
			tabbedPane.remove( panDiscrete );
		}
		else
		{
			localClock = new DiscreteClock( initialMillis );
			setupDiscreteBehaviour();
			tabbedPane.remove( panRealtime );
		}
		clockDriver = new ClockDriver( network.getEventBroker(), localClock );

		updateThread = new Thread( new TimelineUpdateTask() );
		updateThread.setDaemon( true );
		updateThread.start();

		setSize( 377, 177 );
		setVisible( true );
	}

	private void setupRealtimeBehaviour()
	{
		btnStart.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				if ( clockRunning )
				{
					clockCtrl.stop();
					clockRunning = false;
					btnStart.setText( "start" );
					txtMultiplier.setEnabled( true );
				}
				else
				{
					try
					{
						double multiplier = Double.parseDouble( txtMultiplier.getText() );
						clockCtrl.start( multiplier );
						clockRunning = true;
						btnStart.setText( "stop" );
						txtMultiplier.setEnabled( false );
					}
					catch ( NumberFormatException ex )
					{
						JOptionPane.showMessageDialog( TimelineWindowPlugin.this,
								"Can't parse multiplier: " + ex.getMessage() );
					}
				}
			}
		} );
	}

	private void setupDiscreteBehaviour()
	{
		btnAdvance.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				try
				{
					long deltaT = Long.parseLong( txtDeltaT.getText() );
					if ( deltaT < 0 )
					{
						JOptionPane.showMessageDialog( TimelineWindowPlugin.this, "Can't move backwards in time!" );
						return;
					}
					clockCtrl.advance( deltaT );
				}
				catch ( NumberFormatException ex )
				{
					JOptionPane.showMessageDialog( TimelineWindowPlugin.this, "Can't parse time: " + ex.getMessage() );
				}
			}
		} );
	}

	private class TimelineUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			while ( true )
			{
				SwingUtilities.invokeLater( new Runnable()
				{

					@Override
					public void run()
					{
						lblCurrentTime.setText( Long.toString( localClock.currentMillis() ) );
					}
				} );
				try
				{
					Thread.sleep( 100 );
				}
				catch ( InterruptedException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void createUIComponents()
	{
		setResizable( false );
		setTitle( "Timeline Window Plugin" );

		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout( springLayout );

		JLabel lblCurrentTimein = new JLabel(
				"<html>Current Time (in millis):<br/><i>since January 1, 1970 UTC</i></html>" );
		lblCurrentTimein.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
		springLayout.putConstraint( SpringLayout.NORTH, lblCurrentTimein, 10, SpringLayout.NORTH, getContentPane() );
		springLayout.putConstraint( SpringLayout.WEST, lblCurrentTimein, 10, SpringLayout.WEST, getContentPane() );
		getContentPane().add( lblCurrentTimein );

		lblCurrentTime = new JLabel( "0" );
		springLayout.putConstraint( SpringLayout.EAST, lblCurrentTime, -10, SpringLayout.EAST, getContentPane() );
		lblCurrentTime.setFont( new Font( "Dialog", Font.BOLD, 16 ) );
		lblCurrentTime.setHorizontalTextPosition( SwingConstants.RIGHT );
		lblCurrentTime.setHorizontalAlignment( SwingConstants.RIGHT );
		springLayout.putConstraint( SpringLayout.NORTH, lblCurrentTime, 0, SpringLayout.NORTH, lblCurrentTimein );
		springLayout.putConstraint( SpringLayout.WEST, lblCurrentTime, 6, SpringLayout.EAST, lblCurrentTimein );
		springLayout.putConstraint( SpringLayout.SOUTH, lblCurrentTime, 0, SpringLayout.SOUTH, lblCurrentTimein );
		getContentPane().add( lblCurrentTime );

		tabbedPane = new JTabbedPane( JTabbedPane.TOP );
		springLayout.putConstraint( SpringLayout.NORTH, tabbedPane, 6, SpringLayout.SOUTH, lblCurrentTimein );
		springLayout.putConstraint( SpringLayout.WEST, tabbedPane, 10, SpringLayout.WEST, getContentPane() );
		springLayout.putConstraint( SpringLayout.SOUTH, tabbedPane, 100, SpringLayout.SOUTH, lblCurrentTimein );
		springLayout.putConstraint( SpringLayout.EAST, tabbedPane, 0, SpringLayout.EAST, lblCurrentTime );
		getContentPane().add( tabbedPane );

		panRealtime = new JPanel();
		tabbedPane.addTab( "Realtime Clock", null, panRealtime, null );
		SpringLayout sl_panRealtime = new SpringLayout();
		panRealtime.setLayout( sl_panRealtime );

		JLabel lblCurrentMultiplier = new JLabel( "Current Speed Multiplier:" );
		sl_panRealtime.putConstraint( SpringLayout.NORTH, lblCurrentMultiplier, 10, SpringLayout.NORTH, panRealtime );
		sl_panRealtime.putConstraint( SpringLayout.WEST, lblCurrentMultiplier, 10, SpringLayout.WEST, panRealtime );
		panRealtime.add( lblCurrentMultiplier );

		txtMultiplier = new JTextField();
		txtMultiplier.setText( "1.0" );
		sl_panRealtime.putConstraint( SpringLayout.NORTH, txtMultiplier, 0, SpringLayout.NORTH, lblCurrentMultiplier );
		sl_panRealtime.putConstraint( SpringLayout.EAST, txtMultiplier, -10, SpringLayout.EAST, panRealtime );
		panRealtime.add( txtMultiplier );
		txtMultiplier.setColumns( 10 );

		btnStart = new JButton( "start" );
		sl_panRealtime.putConstraint( SpringLayout.NORTH, btnStart, 6, SpringLayout.SOUTH, txtMultiplier );
		sl_panRealtime.putConstraint( SpringLayout.EAST, btnStart, 0, SpringLayout.EAST, txtMultiplier );
		panRealtime.add( btnStart );

		panDiscrete = new JPanel();
		tabbedPane.addTab( "Discrete Clock", null, panDiscrete, null );
		SpringLayout sl_panDiscrete = new SpringLayout();
		panDiscrete.setLayout( sl_panDiscrete );

		JLabel lblAdvanceTimeBy = new JLabel( "<html>Advance Time by:<br/><i>milliseconds</i></html>" );
		sl_panDiscrete.putConstraint( SpringLayout.NORTH, lblAdvanceTimeBy, 10, SpringLayout.NORTH, panDiscrete );
		sl_panDiscrete.putConstraint( SpringLayout.WEST, lblAdvanceTimeBy, 10, SpringLayout.WEST, panDiscrete );
		panDiscrete.add( lblAdvanceTimeBy );

		txtDeltaT = new JTextField();
		sl_panDiscrete.putConstraint( SpringLayout.NORTH, txtDeltaT, 0, SpringLayout.NORTH, lblAdvanceTimeBy );
		sl_panDiscrete.putConstraint( SpringLayout.EAST, txtDeltaT, -10, SpringLayout.EAST, panDiscrete );
		panDiscrete.add( txtDeltaT );
		txtDeltaT.setColumns( 10 );

		btnAdvance = new JButton( "advance" );
		sl_panDiscrete.putConstraint( SpringLayout.NORTH, btnAdvance, 3, SpringLayout.SOUTH, txtDeltaT );
		sl_panDiscrete.putConstraint( SpringLayout.EAST, btnAdvance, 0, SpringLayout.EAST, txtDeltaT );
		panDiscrete.add( btnAdvance );
	}
}
