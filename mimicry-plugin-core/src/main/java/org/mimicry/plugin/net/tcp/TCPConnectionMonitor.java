package org.mimicry.plugin.net.tcp;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;

import org.mimicry.EventListener;
import org.mimicry.engine.ApplicationEvent;
import org.mimicry.engine.EventEngine;
import org.mimicry.events.net.tcp.ConnectionEstablishedEvent;

import com.google.common.base.Preconditions;
import com.jgoodies.binding.adapter.AbstractTableAdapter;
import com.jgoodies.binding.adapter.SingleListSelectionAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.common.collect.ArrayListModel;

public class TCPConnectionMonitor extends JDialog
{
	private final EventEngine							broker;
	private final ArrayListModel<TCPConnectionInfo>		model;
	private final SelectionInList<TCPConnectionInfo>	selection;

	public TCPConnectionMonitor(EventEngine broker)
	{
		Preconditions.checkNotNull( broker );
		this.broker = broker;

		model = new ArrayListModel<TCPConnectionInfo>();
		selection = new SelectionInList<TCPConnectionInfo>( (ListModel) model );

		createUIComponents();

		broker.addEventListener( new EventListener()
		{

			@Override
			public void handleEvent( ApplicationEvent evt )
			{
				if ( evt instanceof ConnectionEstablishedEvent )
				{
					ConnectionEstablishedEvent e = (ConnectionEstablishedEvent) evt;
					TCPConnectionInfo connectionInfo = new TCPConnectionInfo( e.getClientAddress(), e
							.getServerAddress() );
					if ( !model.contains( connectionInfo ) )
					{
						model.add( connectionInfo );
					}
				}

			}
		} );

		setTitle( "TCP Connection Monitor" );
		pack();
		setVisible( true );
	}

	private void createUIComponents()
	{
		setMinimumSize( new Dimension( 400, 300 ) );
		setLayout( new BorderLayout() );
		JTable table = new JTable( new TCPConnectionTableAdapter( model, new String[] { "Source", "Destination" } ) );
		table.setSelectionModel( new SingleListSelectionAdapter( selection.getSelectionIndexHolder() ) );
		JScrollPane scrollPane = new JScrollPane( table );
		scrollPane.setPreferredSize( table.getPreferredSize() );
		add( scrollPane, BorderLayout.CENTER );
	}
}

class TCPConnectionTableAdapter extends AbstractTableAdapter
{
	public TCPConnectionTableAdapter(ListModel<TCPConnectionInfo> listModel, String[] columnNames)
	{
		super( listModel, columnNames );
	}

	@Override
	public Object getValueAt( int rowIndex, int columnIndex )
	{
		TCPConnectionInfo connectionInfo = (TCPConnectionInfo) getRow( rowIndex );

		if ( columnIndex == 0 )
		{
			return connectionInfo.getClientAddress().toString();
		}
		else
		{
			return connectionInfo.getServerAddress().toString();
		}
	}
}