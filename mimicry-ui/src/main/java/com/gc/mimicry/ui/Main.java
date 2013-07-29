package com.gc.mimicry.ui;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jidesoft.plaf.LookAndFeelFactory;

public class Main
{
	private static final Logger	logger;
	static
	{
		logger = LoggerFactory.getLogger( Main.class );
	}

	public static void main( String[] args )
	{
		com.jidesoft.utils.Lm.verifyLicense( "Marc-Christian Schulze", "Mimicry", "aS5VMaqDIacp52..:R4waWiGosri1oa1" );
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
				new MainFrame();
			}
		} );
	}
}
