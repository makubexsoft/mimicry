package com.gc.mimicry.ui;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class NodePropertiesPanel extends JPanel
{
    private JTextField textField;

    public NodePropertiesPanel()
    {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        JLabel lblName = new JLabel("Name");
        springLayout.putConstraint(SpringLayout.NORTH, lblName, 10, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, this);
        add(lblName);

        textField = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, textField, 4, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblName);
        springLayout.putConstraint(SpringLayout.EAST, textField, 196, SpringLayout.EAST, lblName);
        add(textField);
        textField.setColumns(10);

        JLabel lblOperatingSystem = new JLabel("Operating System");
        springLayout.putConstraint(SpringLayout.NORTH, lblOperatingSystem, 6, SpringLayout.SOUTH, lblName);
        springLayout.putConstraint(SpringLayout.WEST, lblOperatingSystem, 0, SpringLayout.WEST, lblName);
        add(lblOperatingSystem);

        JComboBox comboBox = new JComboBox();
        springLayout.putConstraint(SpringLayout.NORTH, comboBox, 6, SpringLayout.SOUTH, textField);
        springLayout.putConstraint(SpringLayout.WEST, comboBox, 6, SpringLayout.EAST, lblOperatingSystem);
        springLayout.putConstraint(SpringLayout.EAST, comboBox, 0, SpringLayout.EAST, textField);
        add(comboBox);
    }
}
