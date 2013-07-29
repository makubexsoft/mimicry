package com.gc.mimicry.ui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutDialog extends JDialog
{
	public AboutDialog()
	{
		setTitle( "About Mimicry" );
		getContentPane().setLayout( null );

		JLabel lblNewLabel = new JLabel( "", new ImageIcon( AboutDialog.class.getResource( "/Logo.jpg" ) ),
				SwingConstants.LEFT );
		lblNewLabel.setOpaque( true );
		lblNewLabel.setBackground( Color.WHITE );
		lblNewLabel.setBounds( 0, 0, 375, 119 );
		getContentPane().add( lblNewLabel );
		
		JLabel lblNewLabel_1 = new JLabel(createAboutText());
		lblNewLabel_1.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setVerticalTextPosition(SwingConstants.TOP);
		lblNewLabel_1.setBounds(10, 123, 347, 131);
		getContentPane().add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("close");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton.setBounds(276, 266, 83, 25);
		getContentPane().add(btnNewButton);
		
		setSize( 375,330 );
		setResizable( false );
	}

	private String createAboutText()
	{
		return "<html>&copy; Copyright 2013 Marc-Christian Schulze<br/>All rights reserved.<br/><br/>"+
				"http://www.mimicry-framework.org<br/><br/>Licensed under the Apache License 2.0<br/><br/>"+
				"Version " + PropertyHelper.getValue( PropertyHelper.MIMICRY_VERSION, "<unknown>" ) +
				"</html>";
	}
}
