package org.mimicry;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;

public class BundleDetailsView extends JPanel
{
	private JTextField textField;
	public BundleDetailsView() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblApplicationName = new JLabel("Application Name:");
		springLayout.putConstraint(SpringLayout.NORTH, lblApplicationName, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblApplicationName, 10, SpringLayout.WEST, this);
		add(lblApplicationName);
		
		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField, 0, SpringLayout.NORTH, lblApplicationName);
		springLayout.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblApplicationName);
		add(textField);
		textField.setColumns(10);
		
		JPanel panel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel, 2, SpringLayout.SOUTH, textField);
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, panel, 275, SpringLayout.SOUTH, textField);
		springLayout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, this);
		panel.setBorder(new TitledBorder(null, "Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JRadioButton rdbtnRunnableJar = new JRadioButton("Runnable JAR");
		springLayout.putConstraint(SpringLayout.NORTH, rdbtnRunnableJar, 6, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, rdbtnRunnableJar, 0, SpringLayout.WEST, lblApplicationName);
		add(rdbtnRunnableJar);
		
		JComboBox comboBox = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, 6, SpringLayout.SOUTH, panel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JTree tree = new JTree();
		scrollPane.setViewportView(tree);
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 16, SpringLayout.EAST, rdbtnRunnableJar);
		springLayout.putConstraint(SpringLayout.EAST, comboBox, -10, SpringLayout.EAST, this);
		add(comboBox);
		
		JRadioButton rdbtnMainClass = new JRadioButton("Main Class");
		springLayout.putConstraint(SpringLayout.NORTH, rdbtnMainClass, 6, SpringLayout.SOUTH, rdbtnRunnableJar);
		springLayout.putConstraint(SpringLayout.WEST, rdbtnMainClass, 0, SpringLayout.WEST, lblApplicationName);
		add(rdbtnMainClass);
		
		JComboBox comboBox_1 = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboBox_1, 6, SpringLayout.SOUTH, comboBox);
		springLayout.putConstraint(SpringLayout.WEST, comboBox_1, 33, SpringLayout.EAST, rdbtnMainClass);
		springLayout.putConstraint(SpringLayout.EAST, comboBox_1, -10, SpringLayout.EAST, this);
		add(comboBox_1);
		
		JLabel lblClassPath = new JLabel("Class Path (one entry per line)");
		springLayout.putConstraint(SpringLayout.NORTH, lblClassPath, 6, SpringLayout.SOUTH, rdbtnMainClass);
		springLayout.putConstraint(SpringLayout.WEST, lblClassPath, 0, SpringLayout.WEST, lblApplicationName);
		add(lblClassPath);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane_1, 6, SpringLayout.SOUTH, lblClassPath);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane_1, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane_1, 88, SpringLayout.SOUTH, lblClassPath);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane_1, 564, SpringLayout.WEST, this);
		add(scrollPane_1);
		
		JTextArea textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
	}
}
