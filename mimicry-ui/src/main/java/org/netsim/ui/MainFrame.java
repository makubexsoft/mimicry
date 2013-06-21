package org.netsim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.gc.mimicry.core.messaging.MessagingSystem;
import com.gc.mimicry.core.session.SessionInfo;
import com.gc.mimicry.core.session.controller.SessionBrowser;
import com.gc.mimicry.core.session.controller.SessionBrowserListener;
import com.gc.mimicry.core.session.controller.SessionController;
import com.gc.mimicry.core.session.controller.SimulationController;
import com.gc.mimicry.net.P2PClusterNode;

public class MainFrame extends JFrame
{
	private static final long	serialVersionUID	= 8555014593438015632L;

	private final JPanel				navigationPanel	= new JPanel();

	private final P2PClusterNode		localNode;
	private final MessagingSystem		messaging;
	private final SimulationController	controller;

	public MainFrame(P2PClusterNode localNode, final MessagingSystem messaging, final SimulationController controller)
	{
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// TODO: shutdown app. gracefully
		setTitle( "Mimicry Simulation Manager" );
		this.localNode = localNode;
		this.messaging = messaging;
		this.controller = controller;

		setSize( new Dimension( 1024, 768 ) );
		getContentPane().setLayout( new BorderLayout( 0, 0 ) );

		JSplitPane splitPane = new JSplitPane();
		getContentPane().add( splitPane );

		final Image logo = loadImage( "/Logo.jpg" );
		final JDesktopPane desktopPane = new JDesktopPane()
		{
			private static final long	serialVersionUID	= -5358754425686107247L;

			@Override
			public void paint( Graphics g )
			{
				super.paint( g );
				int x = getWidth() - logo.getWidth( null ) + 20;
				int y = getHeight() - logo.getHeight( null ) - 20;
				g.setColor( new Color( 255, 255, 255, 128 ) );
				g.drawImage( logo, x, y, null );
				g.fillRect( x, y, logo.getWidth( null ), logo.getHeight( null ) );
				paintChildren( g );
			}
		};
		desktopPane.setBackground( new Color( 255, 255, 255 ) );

		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment( FlowLayout.RIGHT );

		JButton btnNewSession = new JButton( "New Session..." );
		btnNewSession.addActionListener( new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				SessionCreationFrame frame = new SessionCreationFrame( controller );
				frame.setVisible( true );
				desktopPane.add( frame );
			}
		} );
		buttonPanel.add( btnNewSession );

		JScrollPane scrollPane = new JScrollPane();
		final JTree tree = new JTree( new SessionModel( controller.getSessionBrowser() ) );
		tree.setCellRenderer( new SessionRenderer() );
		tree.addMouseListener( new MouseAdapter()
		{
			@Override
			public void mouseClicked( MouseEvent e )
			{
				if ( e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1 )
				{
					TreePath path = tree.getSelectionPath();
					if ( path != null )
					{
						Object object = path.getLastPathComponent();
						if ( object instanceof SessionInfo )
						{
							SessionInfo info = (SessionInfo) object;
							SessionController sessionController;
							sessionController = new SessionController( info.getSessionId(), info.getParticipants(),
									messaging );
							SessionManagementFrame frame = new SessionManagementFrame( sessionController );
							frame.setVisible( true );
							desktopPane.add( frame );
						}
					}
				}
				super.mouseClicked( e );
			}
		} );
		scrollPane.setViewportView( tree );
		navigationPanel.setLayout( new BorderLayout( 0, 0 ) );
		navigationPanel.add( buttonPanel, BorderLayout.NORTH );
		navigationPanel.add( scrollPane, BorderLayout.CENTER );

		splitPane.setLeftComponent( navigationPanel );
		splitPane.setRightComponent( desktopPane );
		splitPane.setDividerLocation( 350 );

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder( new BevelBorder( BevelBorder.LOWERED, null, null, null, null ) );
		getContentPane().add( statusPanel, BorderLayout.SOUTH );
		statusPanel.setLayout( new BorderLayout( 0, 0 ) );

		JLabel lblNewLabel = new JLabel( String.format( "LocalNode: id=%s, architecture=%s, os=%s, kernel=%s, java=%s",
				localNode.getNodeInfo().getNodeId(), localNode.getNodeInfo().getArchitecture(), localNode.getNodeInfo()
						.getOperatingSystem(), localNode.getNodeInfo().getOsVersion(), localNode.getNodeInfo()
						.getJavaVersion() ) );
		lblNewLabel.setFont( new Font( "Dialog", Font.PLAIN, 12 ) );
		lblNewLabel.setHorizontalTextPosition( SwingConstants.CENTER );
		lblNewLabel.setHorizontalAlignment( SwingConstants.LEFT );

		statusPanel.add( lblNewLabel );

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar( menuBar );

		JMenu mnNewMenu = new JMenu( "File" );
		menuBar.add( mnNewMenu );

		JMenuItem mntmNewMenuItem = new JMenuItem( "Exit" );
		mnNewMenu.add( mntmNewMenuItem );
		mntmNewMenuItem.addActionListener( new ActionListener()
		{

			public void actionPerformed( ActionEvent e )
			{
				// TODO: shutdown app. gracefully
				System.exit( 0 );
			}
		} );
	}

	private Image loadImage( String path )
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read( getClass().getResourceAsStream( path ) );
		}
		catch ( IOException e )
		{
		}
		return img;
	}
}

class SessionRenderer extends DefaultTreeCellRenderer
{
	private static final long	serialVersionUID	= 8219916846229013574L;

	@Override
	public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus )
	{

		if ( value instanceof SessionInfo )
		{
			SessionInfo sessionInfo = (SessionInfo) value;
			value = sessionInfo.getSessionId().toString().toUpperCase();
		}
		return super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
	}
}

class SessionModel implements TreeModel, SessionBrowserListener, Closeable
{

	private final CopyOnWriteArrayList<TreeModelListener>	listener;
	private final String									root	= "Sessions";
	private final SessionBrowser							browser;

	public SessionModel(SessionBrowser browser)
	{
		this.browser = browser;
		listener = new CopyOnWriteArrayList<TreeModelListener>();
		browser.addSessionBrowserListener( this );
		browser.querySessions();
	}

	public void close()
	{
		browser.removeSessionBrowserListener( this );
	}

	public Object getRoot()
	{
		return root;
	}

	public Object getChild( Object parent, int index )
	{
		if ( browser == null )
		{
			return null;
		}
		return browser.getSessions().get( index );
	}

	public int getChildCount( Object parent )
	{
		if ( browser == null )
		{
			return 0;
		}
		if ( parent == getRoot() )
		{
			return browser.getSessions().size();
		}
		return 0;
	}

	public boolean isLeaf( Object node )
	{
		if ( node == getRoot() )
		{
			return browser.getSessions().size() == 0;
		}
		return true;
	}

	public void valueForPathChanged( TreePath path, Object newValue )
	{
	}

	public int getIndexOfChild( Object parent, Object child )
	{
		if ( child instanceof SessionInfo )
		{
			return browser.getSessions().indexOf( child );
		}
		return 0;
	}

	public void addTreeModelListener( TreeModelListener l )
	{
		listener.add( l );
	}

	public void removeTreeModelListener( TreeModelListener l )
	{
		listener.remove( l );
	}

	public void sessionAdded( int index, SessionBrowser browser, SessionInfo session )
	{
		for ( TreeModelListener l : listener )
		{
			l.treeNodesInserted( new TreeModelEvent( this, new TreePath( new Object[]
			{ getRoot(), session } ) ) );
		}
	}

	public void sessionRemoved( int index, SessionBrowser browser, SessionInfo session )
	{
		for ( TreeModelListener l : listener )
		{
			l.treeNodesRemoved( new TreeModelEvent( this, new TreePath( new Object[]
			{ getRoot(), session } ) ) );
		}
	}
}