package com.gc.mimicry.plugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import com.gc.mimicry.core.event.EventBroker;
import com.gc.mimicry.core.event.EventListener;
import com.gc.mimicry.core.runtime.ApplicationRef;
import com.gc.mimicry.shared.events.Event;
import com.gc.mimicry.shared.events.console.ConsoleInputEvent;
import com.gc.mimicry.shared.events.console.ConsoleOutputEvent;

/**
 * This plugin allows the user to interact with the command line of a certain
 * application using a graphical user interface.
 * 
 * @author Marc-Christian Schulze
 * @see ConsoleInputEvent
 * @see ConsoleOutputEvent
 */
public class ConsoleWindowPlugin extends JFrame implements EventListener
{
	private static final long		serialVersionUID	= 120529917436479426L;
	private final EventBroker		broker;
	private final ApplicationRef	appRef;
	private JScrollPane				scrollPane;
	private JTextArea				consoleOutput;
	private JTextField				inputField;

	private ConsoleWindowPlugin(EventBroker broker, ApplicationRef appRef)
	{
		setTitle( "Console of Application[" + appRef.getApplicationId() + "]" );
		this.broker = broker;
		this.appRef = appRef;
		setLayout( new BorderLayout() );
		createComponents();
		setSize( 600, 800 );
		setVisible( true );
	}

	public static void attach( EventBroker broker, ApplicationRef appRef )
	{
		ConsoleWindowPlugin window = new ConsoleWindowPlugin( broker, appRef );
		broker.addEventListener( window );
	}

	private void createComponents()
	{
		consoleOutput = new JTextArea();
		consoleOutput.setEditable( false );
		DefaultCaret caret = (DefaultCaret) consoleOutput.getCaret();
		caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );

		scrollPane = new JScrollPane( consoleOutput );
		inputField = new JTextField();

		add( scrollPane, BorderLayout.CENTER );
		add( inputField, BorderLayout.SOUTH );

		inputField.addActionListener( new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				String input = inputField.getText() + "\n";
				ConsoleInputEvent in = new ConsoleInputEvent( input.getBytes() );
				in.setTargetApp( appRef.getApplicationId() );
				broker.fireEvent( in, ConsoleWindowPlugin.this );
				consoleOutput.append( input );
				inputField.setText( "" );
			}
		} );
	}

	@Override
	public void handleEvent( Event evt )
	{
		if ( evt instanceof ConsoleOutputEvent )
		{
			final ConsoleOutputEvent out = (ConsoleOutputEvent) evt;
			if ( out.getSourceApplication() == appRef.getApplicationId() )
			{
				SwingUtilities.invokeLater( new Runnable()
				{

					@Override
					public void run()
					{
						consoleOutput.append( out.getData() );
					}
				} );
			}
		}
	}
}
