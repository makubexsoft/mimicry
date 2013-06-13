package org.netsim.ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.messaging.p2p.DefaultMessagingSystem;
import com.gc.mimicry.core.session.controller.SimulationController;
import com.gc.mimicry.net.P2PClusterNode;

public class Main {
	public static void main( String[] args ) {
		final P2PClusterNode localNode = new P2PClusterNode();
		final MessagingSystem messaging = new DefaultMessagingSystem( localNode );
		final SimulationController controller = new SimulationController( messaging );

		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch ( Exception e ) {
		}

		SwingUtilities.invokeLater( new Runnable() {

			public void run() {
				new MainFrame( localNode, messaging, controller ).setVisible( true );
			}
		} );
	}
}
