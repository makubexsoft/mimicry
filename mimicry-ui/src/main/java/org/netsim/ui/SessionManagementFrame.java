package org.netsim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import com.gc.mimicry.cluster.NodeInfo;
import com.gc.mimicry.cluster.session.controller.SessionController;
import com.gc.mimicry.engine.timing.ClockType;
import com.jidesoft.swing.JideTabbedPane;

public class SessionManagementFrame extends JInternalFrame
{
	private final SessionController	controller;
	private final JTextField		textField;

	public SessionManagementFrame(SessionController controller)
	{
		setResizable( true );
		setClosable( true );
		setSize( new Dimension( 523, 503 ) );
		setTitle( "Session " + controller.getSessionId() );
		this.controller = controller;
		getContentPane().setLayout( new BorderLayout( 0, 0 ) );

		JTabbedPane tabbedPane = new JideTabbedPane( JTabbedPane.TOP );
		getContentPane().add( tabbedPane, BorderLayout.CENTER );

		JPanel panNodes = new JPanel();
		tabbedPane.addTab( "Cluster Nodes", null, panNodes, null );
		panNodes.setLayout( new BorderLayout( 0, 0 ) );

		JXTreeTable treeTable = new JXTreeTable(
				new ClusterNodesModel( new ArrayList<NodeInfo>( controller.getNodes() ) ) );
		treeTable.setShowsRootHandles( false );

		JScrollPane tableScroll = new JScrollPane();
		tableScroll.setViewportView( treeTable );
		panNodes.add( tableScroll, BorderLayout.CENTER );

		JPanel panClock = new JPanel();
		tabbedPane.addTab( "Clock", null, panClock, null );
		panClock.setLayout( new BorderLayout( 0, 0 ) );

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight( 0.5 );
		splitPane.setOrientation( JSplitPane.VERTICAL_SPLIT );
		panClock.add( splitPane, BorderLayout.CENTER );

		JPanel panel = new JPanel();
		panel.setBorder( new TitledBorder( null, "Clock Events", TitledBorder.CENTER, TitledBorder.TOP, null, null ) );
		splitPane.setRightComponent( panel );
		panel.setLayout( new BorderLayout( 0, 0 ) );

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		panel.add( scrollPane, BorderLayout.CENTER );

		JTextArea txtClockEvents = new JTextArea();
		txtClockEvents.setEditable( false );
		scrollPane.setViewportView( txtClockEvents );

		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent( panel_1 );
		panel_1.setLayout( new BorderLayout( 0, 0 ) );

		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment( FlowLayout.LEFT );
		panel_1.add( panel_2, BorderLayout.NORTH );

		JLabel lblInstalledClock = new JLabel( "Installed Clock" );
		panel_2.add( lblInstalledClock );

		JComboBox<ClockType> comboBox = new JComboBox<ClockType>();
		comboBox.setModel( new DefaultComboBoxModel<ClockType>( ClockType.values() ) );
		panel_2.add( comboBox );

		JButton btnInstall = new JButton( "install" );
		btnInstall.setEnabled( false );
		panel_2.add( btnInstall );

		JPanel panel_4 = new JPanel();
		panel_1.add( panel_4, BorderLayout.CENTER );
		SpringLayout sl_panel_4 = new SpringLayout();
		panel_4.setLayout( sl_panel_4 );

		JButton btnStartClock = new JButton( "Start Clock" );
		sl_panel_4.putConstraint( SpringLayout.SOUTH, btnStartClock, -10, SpringLayout.SOUTH, panel_4 );
		panel_4.add( btnStartClock );

		JLabel lblTimeMultiplier = new JLabel( "Time Multiplier" );
		sl_panel_4.putConstraint( SpringLayout.WEST, btnStartClock, 0, SpringLayout.WEST, lblTimeMultiplier );
		sl_panel_4.putConstraint( SpringLayout.WEST, lblTimeMultiplier, 10, SpringLayout.WEST, panel_4 );
		sl_panel_4.putConstraint( SpringLayout.NORTH, lblTimeMultiplier, 10, SpringLayout.NORTH, panel_4 );
		panel_4.add( lblTimeMultiplier );

		textField = new JTextField();
		textField.setText( "1.0" );
		sl_panel_4.putConstraint( SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblTimeMultiplier );
		sl_panel_4.putConstraint( SpringLayout.SOUTH, textField, 0, SpringLayout.SOUTH, lblTimeMultiplier );
		panel_4.add( textField );
		textField.setColumns( 10 );

		JPanel panel_5 = new JPanel();
		tabbedPane.addTab( "Nodes & Applications", null, panel_5, null );
		panel_5.setLayout( new BorderLayout( 0, 0 ) );

		JScrollPane scrollPane_1 = new JScrollPane();
		panel_5.add( scrollPane_1, BorderLayout.CENTER );

		JXTreeTable treeTable_1 = new JXTreeTable( new LogicalNodesModel() );
		scrollPane_1.setViewportView( treeTable_1 );

		JPanel panel_3 = new JPanel();
		panel_3.setBorder( new TitledBorder( null, "Status", TitledBorder.CENTER, TitledBorder.TOP, null, null ) );
		getContentPane().add( panel_3, BorderLayout.EAST );
		panel_3.setLayout( new BoxLayout( panel_3, BoxLayout.Y_AXIS ) );

		JLabel lblSimulationTime = new JLabel( "Simulation Time:" );
		panel_3.add( lblSimulationTime );

		JLabel label_1 = new JLabel( "01.01.1900" );
		panel_3.add( label_1 );

		JLabel label = new JLabel( "00:00:00.000" );
		panel_3.add( label );

		JSeparator separator = new JSeparator();
		panel_3.add( separator );
	}
}

class LogicalNodesModel implements TreeTableModel
{
	public LogicalNodesModel()
	{

	}

	public Object getRoot()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Object getChild( Object parent, int index )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getChildCount( Object parent )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isLeaf( Object node )
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void valueForPathChanged( TreePath path, Object newValue )
	{
		// TODO Auto-generated method stub

	}

	public int getIndexOfChild( Object parent, Object child )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void addTreeModelListener( TreeModelListener l )
	{
		// TODO Auto-generated method stub

	}

	public void removeTreeModelListener( TreeModelListener l )
	{
		// TODO Auto-generated method stub

	}

	public Class<?> getColumnClass( int arg0 )
	{
		return String.class;
	}

	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName( int arg0 )
	{
		return COLUMN_NAMES[arg0];
	}

	public int getHierarchicalColumn()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getValueAt( Object arg0, int arg1 )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCellEditable( Object arg0, int arg1 )
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setValueAt( Object arg0, Object arg1, int arg2 )
	{
		// TODO Auto-generated method stub

	}

	private static final String[]	COLUMN_NAMES	= new String[]
													{ "Node / Application", "State" };
}

class ClusterNodesModel implements TreeTableModel
{

	private final static String[]	COLUMN_NAMES	= new String[]
													{ "Node", "IP-Address", "Architecture", "Operating System",
			"Kernel", "# CPU Cores", "Java Version" };
	private final Object			root			= new Object();
	private final List<NodeInfo>	nodes;

	public ClusterNodesModel(List<NodeInfo> nodes)
	{
		this.nodes = nodes;
	}

	public Object getRoot()
	{
		return root;
	}

	public Object getChild( Object parent, int index )
	{
		if ( parent == getRoot() )
		{
			return nodes.get( index );
		}
		return null;
	}

	public int getChildCount( Object parent )
	{
		if ( nodes == null )
		{
			return 0;
		}
		if ( getRoot() == parent )
		{
			return nodes.size();
		}
		return 0;
	}

	public boolean isLeaf( Object node )
	{
		if ( getRoot() == node )
		{
			return nodes.size() == 0;
		}
		return true;
	}

	public void valueForPathChanged( TreePath path, Object newValue )
	{
	}

	public int getIndexOfChild( Object parent, Object child )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void addTreeModelListener( TreeModelListener l )
	{
		// TODO Auto-generated method stub
	}

	public void removeTreeModelListener( TreeModelListener l )
	{
		// TODO Auto-generated method stub
	}

	public Class<?> getColumnClass( int arg0 )
	{
		return String.class;
	}

	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName( int arg0 )
	{
		return COLUMN_NAMES[arg0];
	}

	public int getHierarchicalColumn()
	{
		return 0;
	}

	public Object getValueAt( Object node, int column )
	{
		if ( node instanceof NodeInfo )
		{
			NodeInfo info = (NodeInfo) node;
			switch ( column )
			{
				case 0:
					return info.getNodeId().toString().toUpperCase();
				case 1:
					return info.getIpAddress().toString();
				case 2:
					return info.getArchitecture();
				case 3:
					return info.getOperatingSystem();
				case 4:
					return info.getOsVersion();
				case 5:
					return info.getNumberCores();
				case 6:
					return info.getJavaVersion();
			}
		}

		return null;
	}

	public boolean isCellEditable( Object arg0, int arg1 )
	{
		return false;
	}

	public void setValueAt( Object arg0, Object arg1, int arg2 )
	{
	}
}
