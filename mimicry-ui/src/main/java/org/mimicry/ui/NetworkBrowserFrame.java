package org.mimicry.ui;

import com.jidesoft.docking.DockableFrame;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class NetworkBrowserFrame extends DockableFrame
{
	public NetworkBrowserFrame() {
		setTitle("Network Browser");
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Simulated Network") {
				{
					DefaultMutableTreeNode node_1;
					node_1 = new DefaultMutableTreeNode("Node-A");
						node_1.add(new DefaultMutableTreeNode("App-1"));
						node_1.add(new DefaultMutableTreeNode("App-2"));
						node_1.add(new DefaultMutableTreeNode("App-3"));
						node_1.add(new DefaultMutableTreeNode("App-4"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Node-B");
						node_1.add(new DefaultMutableTreeNode("App-1"));
						node_1.add(new DefaultMutableTreeNode("App-2"));
						node_1.add(new DefaultMutableTreeNode("App-3"));
						node_1.add(new DefaultMutableTreeNode("App-4"));
					add(node_1);
					node_1 = new DefaultMutableTreeNode("Node-C");
						node_1.add(new DefaultMutableTreeNode("App-1"));
					add(node_1);
				}
			}
		));
		scrollPane.setViewportView(tree);
	}

}
