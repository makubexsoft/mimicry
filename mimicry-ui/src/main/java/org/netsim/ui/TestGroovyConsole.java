package org.netsim.ui;

import groovy.lang.Closure;
import groovy.ui.Console;
import groovy.ui.ConsoleTextEditor;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class TestGroovyConsole extends JFrame
{

	public TestGroovyConsole()
	{
		setLayout( new BorderLayout() );
		
		
		ConsoleTextEditor editor = new ConsoleTextEditor();
		add(editor, BorderLayout.SOUTH);
		
		JDesktopPane desktop = new JDesktopPane();
		
		JPanel panConsole = new JPanel();
		panConsole.setBorder( new TitledBorder( "test" ) );
		panConsole.setLayout( new BorderLayout() );
		panConsole.add(desktop, BorderLayout.CENTER);
		add(panConsole, BorderLayout.CENTER);
		
		final JInternalFrame cframe = new JInternalFrame();
		desktop.add( cframe );
		cframe.setBorder( null );
		BasicInternalFrameUI bi = (BasicInternalFrameUI)cframe.getUI();
		bi.setNorthPane( null );
		//cframe.setSize( 400,300 );
		try
		{
			cframe.setMaximum( true );
		}
		catch ( PropertyVetoException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cframe.setVisible( true );
		
		Map<String, Object> config = new HashMap<String, Object>();
		config.put( "rootContainerDelegate", new Closure<JInternalFrame>(this)
		{
			@Override
			public JInternalFrame call()
			{
				return cframe;
			}
		} );
		config.put( "menuBarDelegate", new Closure<JInternalFrame>(this)
		{
			@Override
			public JInternalFrame call( Object... args )
			{
				return cframe;
			}
		} );
		
		Console console = new Console();
		//console.setShowToolbar( false );
		console.run( config );
		
		
		setSize(800,600);
		setVisible( true );
		
//		rootContainerDelegate:{
//        frame(
//            title: 'GroovyConsole',
//            //location: [100,100], // in groovy 2.0 use platform default location
//            iconImage: imageIcon("/groovy/ui/ConsoleIcon.png").image,
//            defaultCloseOperation: JFrame.DO_NOTHING_ON_CLOSE,
//        ) {
//            try {
//                current.locationByPlatform = true
//            } catch (Exception e) {
//                current.location = [100, 100] // for 1.4 compatibility
//            }
//            containingWindows += current
//        }
//    },
//    menuBarDelegate: {arg->
//        current.JMenuBar = build(arg)}
	}
	
	public static void main( String[] args )
	{
//		try
//		{
//			UIManager.setLookAndFeel( "com.seaglasslookandfeel.SeaGlassLookAndFeel" );
//		}
//		catch ( ClassNotFoundException e )
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch ( InstantiationException e )
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch ( IllegalAccessException e )
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch ( UnsupportedLookAndFeelException e )
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		new TestGroovyConsole();
	}
}
