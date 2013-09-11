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

import com.gc.mimicry.engine.EventBroker;
import com.gc.mimicry.engine.EventListener;
import com.gc.mimicry.engine.apps.ApplicationRef;
import com.gc.mimicry.engine.event.DefaultEventFactory;
import com.gc.mimicry.engine.event.Event;
import com.gc.mimicry.engine.event.EventFactory;
import com.gc.mimicry.engine.event.Identity;
import com.gc.mimicry.ext.stdio.events.ConsoleStdinEvent;
import com.gc.mimicry.ext.stdio.events.ConsoleStdoutEvent;

/**
 * This plugin allows the user to interact with the command line of a certain
 * application using a graphical user interface.
 * 
 * @author Marc-Christian Schulze
 * @see ConsoleStdinEvent
 * @see ConsoleStdoutEvent
 */
public class ConsoleWindowPlugin extends JFrame implements EventListener
{
	private static final long		serialVersionUID	= 120529917436479426L;
	private final EventBroker		broker;
	private final ApplicationRef	appRef;
	private JScrollPane				scrollPane;
	private JTextArea				consoleOutput;
	private JTextField				inputField;
	private final Identity			identity;
	private final EventFactory		eventFactory;

	private ConsoleWindowPlugin(EventBroker broker, ApplicationRef appRef)
	{
		setTitle( "Console of Application[" + appRef.getApplicationId() + "]" );
		this.broker = broker;
		this.appRef = appRef;

		identity = Identity.create( getTitle() );
		eventFactory = DefaultEventFactory.create( identity );

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

				ConsoleStdinEvent event = eventFactory.createEvent( ConsoleStdinEvent.class, appRef.getApplicationId() );
				event.setData( input.getBytes() );
				broker.fireEvent( event, ConsoleWindowPlugin.this );

				consoleOutput.append( input );
				inputField.setText( "" );
			}
		} );
	}

	@Override
	public void handleEvent( Event evt )
	{
		if ( evt instanceof ConsoleStdoutEvent )
		{
			final ConsoleStdoutEvent out = (ConsoleStdoutEvent) evt;
			if ( out.getSourceApplication() == appRef.getApplicationId() )
			{
				SwingUtilities.invokeLater( new Runnable()
				{

					@Override
					public void run()
					{
						consoleOutput.append( new String( out.getData() ) );
					}
				} );
			}
		}
	}
}
