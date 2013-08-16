package com.gc.mimicry.ui;

import com.jidesoft.docking.DockableFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.AbstractListModel;

public class ApplicationRepositoryFrame extends DockableFrame
{
	public ApplicationRepositoryFrame()
	{
		setTitle( "Application Repository" );
		
		JScrollPane scrollPane = new JScrollPane();
		getRootPane().getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"PingPong-TCP", "PingPong-UDP"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane.setViewportView(list);
	}
}
