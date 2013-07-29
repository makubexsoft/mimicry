package org.netsim.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import com.gc.mimicry.cluster.session.controller.SessionController;
import com.gc.mimicry.cluster.session.controller.SessionControllerFuture;
import com.gc.mimicry.cluster.session.controller.SimulationController;
import com.gc.mimicry.util.concurrent.FutureListener;
import com.gc.mimicry.util.concurrent.ValueFuture;

public class SessionCreationFrame extends JInternalFrame implements FutureListener<ValueFuture<SessionController>> {

	private static final long	serialVersionUID	= -6894927518137463903L;
	private SessionControllerFuture	future;
	private final JTextField		textField;

	public SessionCreationFrame(final SimulationController controller) {
		setResizable( true );
		setTitle( "Create New Session" );
		setClosable( true );
		setSize( new Dimension( 330, 320 ) );
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout( springLayout );

		JLabel lblNodesTo = new JLabel( "# Nodes to wait for:" );
		springLayout.putConstraint( SpringLayout.NORTH, lblNodesTo, 10, SpringLayout.NORTH, getContentPane() );
		springLayout.putConstraint( SpringLayout.WEST, lblNodesTo, 10, SpringLayout.WEST, getContentPane() );
		getContentPane().add( lblNodesTo );

		textField = new JTextField();
		springLayout.putConstraint( SpringLayout.NORTH, textField, -2, SpringLayout.NORTH, lblNodesTo );
		springLayout.putConstraint( SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblNodesTo );
		getContentPane().add( textField );
		textField.setColumns( 10 );

		JLabel lblNewLabel = new JLabel( "Nodes already confirmed to participate:" );
		springLayout.putConstraint( SpringLayout.NORTH, lblNewLabel, 6, SpringLayout.SOUTH, lblNodesTo );
		springLayout.putConstraint( SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, getContentPane() );
		getContentPane().add( lblNewLabel );

		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint( SpringLayout.NORTH, scrollPane, 6, SpringLayout.SOUTH, lblNewLabel );
		springLayout.putConstraint( SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane() );
		springLayout.putConstraint( SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, getContentPane() );
		getContentPane().add( scrollPane );

		JList list = new JList();
		scrollPane.setViewportView( list );

		final JButton btnNewButton = new JButton( "Start Creation" );
		btnNewButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				try {
					int numRequiredNodes = Integer.parseInt( textField.getText() );
					btnNewButton.setText( "Waiting for Nodes..." );
					btnNewButton.setEnabled( false );
					future = controller.createSession( numRequiredNodes );
					future.addFutureListener( SessionCreationFrame.this );
				} catch ( NumberFormatException ex ) {

				}
			}
		} );
		springLayout.putConstraint( SpringLayout.EAST, btnNewButton, -10, SpringLayout.EAST, getContentPane() );
		springLayout.putConstraint( SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, btnNewButton );
		springLayout.putConstraint( SpringLayout.SOUTH, btnNewButton, -10, SpringLayout.SOUTH, getContentPane() );
		getContentPane().add( btnNewButton );

		JButton btnNewButton_1 = new JButton( "Abort" );
		btnNewButton_1.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				dispose();
			}
		} );
		springLayout.putConstraint( SpringLayout.NORTH, btnNewButton_1, 6, SpringLayout.SOUTH, scrollPane );
		springLayout.putConstraint( SpringLayout.WEST, btnNewButton_1, 0, SpringLayout.WEST, lblNodesTo );
		getContentPane().add( btnNewButton_1 );
	}

	public void operationComplete( final ValueFuture<SessionController> future ) {
		future.removeFutureListener( this );
		if ( future.isSuccess() ) {
			SwingUtilities.invokeLater( new Runnable() {

				public void run() {
					SessionManagementFrame frame = new SessionManagementFrame( future.getValue() );
					frame.setVisible( true );
					getDesktopPane().add( frame );
					dispose();
				}
			} );
		}
	}
}
