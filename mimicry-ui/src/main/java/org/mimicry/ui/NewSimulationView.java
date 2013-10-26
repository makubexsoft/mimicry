package org.mimicry.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import org.mimicry.timing.TimelineType;


public class NewSimulationView extends JPanel
{
    private static final long serialVersionUID = 848650469544142535L;
    private JTextField txtRealtimeMultiplier;
    private JTextField txtRealtimeStart;
    private JTextField txtDiscreteStart;
    private JButton btnCancel;
    private JButton btnCreate;
    private JCheckBox chkDeterministic;
    private JRadioButton radioDiscrete;
    private JRadioButton radioRealtime;
    private JRadioButton radioSystem;

    public NewSimulationView()
    {
        SpringLayout springLayout = new SpringLayout();
        setLayout(springLayout);

        setPreferredSize(new Dimension(530, 560));

        JLabel lblCreateANew = new JLabel("  Create a new Simulation...");
        springLayout.putConstraint(SpringLayout.NORTH, lblCreateANew, 0, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, lblCreateANew, 0, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, lblCreateANew, 535, SpringLayout.WEST, this);
        lblCreateANew.setHorizontalAlignment(SwingConstants.LEFT);
        lblCreateANew.setHorizontalTextPosition(SwingConstants.LEFT);
        lblCreateANew.setOpaque(true);
        lblCreateANew.setBackground(Color.WHITE);
        lblCreateANew.setFont(new Font("Dialog", Font.BOLD, 18));
        add(lblCreateANew);

        JLabel lblNewLabel = new JLabel("What kind of timeline do you want to use?");
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 72, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.SOUTH, lblCreateANew, -6, SpringLayout.NORTH, lblNewLabel);
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, this);
        add(lblNewLabel);

        radioSystem = new JRadioButton("<html><b>System</b><br/>The time of the JVM.</html>");
        radioSystem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                radioDiscrete.setSelected(!radioSystem.isSelected());
                radioRealtime.setSelected(!radioSystem.isSelected());
                txtDiscreteStart.setEnabled(radioDiscrete.isSelected());
                txtRealtimeMultiplier.setEnabled(radioRealtime.isSelected());
                txtRealtimeStart.setEnabled(radioRealtime.isSelected());
                chkDeterministic.setEnabled(radioDiscrete.isSelected());
                if (!radioDiscrete.isSelected())
                {
                    chkDeterministic.setSelected(false);
                }
            }
        });
        springLayout.putConstraint(SpringLayout.WEST, radioSystem, 72, SpringLayout.WEST, this);
        radioSystem.setSelected(true);
        radioSystem.setFont(new Font("Dialog", Font.PLAIN, 12));
        springLayout.putConstraint(SpringLayout.NORTH, radioSystem, 6, SpringLayout.SOUTH, lblNewLabel);
        add(radioSystem);

        radioRealtime = new JRadioButton(
                "<html><b>Realtime</b><br/>The time of the JVM but stretched by a given multiplier..</html>");
        radioRealtime.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                radioDiscrete.setSelected(!radioRealtime.isSelected());
                radioSystem.setSelected(!radioRealtime.isSelected());
                txtRealtimeMultiplier.setEnabled(radioRealtime.isSelected());
                txtRealtimeStart.setEnabled(radioRealtime.isSelected());
                txtDiscreteStart.setEnabled(radioDiscrete.isSelected());
                chkDeterministic.setEnabled(radioDiscrete.isSelected());
                if (!radioDiscrete.isSelected())
                {
                    chkDeterministic.setSelected(false);
                }
            }
        });
        springLayout.putConstraint(SpringLayout.NORTH, radioRealtime, 6, SpringLayout.SOUTH, radioSystem);
        springLayout.putConstraint(SpringLayout.WEST, radioRealtime, 0, SpringLayout.WEST, radioSystem);
        radioRealtime.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(radioRealtime);

        radioDiscrete = new JRadioButton("<html><b>Discrete</b><br/>The timeline must be advanced manually.</html>");
        radioDiscrete.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                radioRealtime.setSelected(!radioDiscrete.isSelected());
                radioSystem.setSelected(!radioDiscrete.isSelected());
                txtDiscreteStart.setEnabled(radioDiscrete.isSelected());
                txtRealtimeMultiplier.setEnabled(radioRealtime.isSelected());
                txtRealtimeStart.setEnabled(radioRealtime.isSelected());
                chkDeterministic.setEnabled(radioDiscrete.isSelected());
                if (!radioDiscrete.isSelected())
                {
                    chkDeterministic.setSelected(false);
                }
            }
        });
        springLayout.putConstraint(SpringLayout.WEST, radioDiscrete, 0, SpringLayout.WEST, radioSystem);
        radioDiscrete.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(radioDiscrete);

        JLabel lblNewLabel_1 = new JLabel("Do you need deterministic thread scheduling?");
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 10, SpringLayout.WEST, this);
        add(lblNewLabel_1);

        chkDeterministic = new JCheckBox(
                "<html><b>Enable deterministic thread scheduling</b><br/>Requires a discrete time line.</html>");
        chkDeterministic.setEnabled(false);
        springLayout.putConstraint(SpringLayout.NORTH, chkDeterministic, 6, SpringLayout.SOUTH, lblNewLabel_1);
        springLayout.putConstraint(SpringLayout.WEST, chkDeterministic, 0, SpringLayout.WEST, radioSystem);
        chkDeterministic.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(chkDeterministic);

        JLabel lblNewLabel_2 = new JLabel("Specify the multiplier");
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_2, 92, SpringLayout.WEST, this);
        lblNewLabel_2.setFont(new Font("Dialog", Font.PLAIN, 12));
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_2, 6, SpringLayout.SOUTH, radioRealtime);
        add(lblNewLabel_2);

        btnCreate = new JButton("Create");
        springLayout.putConstraint(SpringLayout.SOUTH, btnCreate, -10, SpringLayout.SOUTH, this);
        springLayout.putConstraint(SpringLayout.EAST, btnCreate, -10, SpringLayout.EAST, this);
        add(btnCreate);

        btnCancel = new JButton("Cancel");
        springLayout.putConstraint(SpringLayout.SOUTH, btnCancel, 0, SpringLayout.SOUTH, btnCreate);
        springLayout.putConstraint(SpringLayout.EAST, btnCancel, -6, SpringLayout.WEST, btnCreate);
        add(btnCancel);

        txtRealtimeMultiplier = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, txtRealtimeMultiplier, 6, SpringLayout.SOUTH, lblNewLabel_2);
        springLayout.putConstraint(SpringLayout.WEST, txtRealtimeMultiplier, 92, SpringLayout.WEST, this);
        springLayout.putConstraint(SpringLayout.EAST, txtRealtimeMultiplier, 266, SpringLayout.WEST, this);
        txtRealtimeMultiplier.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRealtimeMultiplier.setEnabled(false);
        txtRealtimeMultiplier.setText("1.0");
        add(txtRealtimeMultiplier);
        txtRealtimeMultiplier.setColumns(10);

        JLabel lblNewLabel_3 = new JLabel("Specify the start time of the timeline in milliseconds");
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_3, 6, SpringLayout.SOUTH, txtRealtimeMultiplier);
        springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_3, 0, SpringLayout.WEST, lblNewLabel_2);
        lblNewLabel_3.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(lblNewLabel_3);

        txtRealtimeStart = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, radioDiscrete, 6, SpringLayout.SOUTH, txtRealtimeStart);
        txtRealtimeStart.setHorizontalAlignment(SwingConstants.RIGHT);
        txtRealtimeStart.setEnabled(false);
        txtRealtimeStart.setText("0");
        springLayout.putConstraint(SpringLayout.NORTH, txtRealtimeStart, 6, SpringLayout.SOUTH, lblNewLabel_3);
        springLayout.putConstraint(SpringLayout.WEST, txtRealtimeStart, 0, SpringLayout.WEST, lblNewLabel_2);
        springLayout.putConstraint(SpringLayout.EAST, txtRealtimeStart, 0, SpringLayout.EAST, txtRealtimeMultiplier);
        add(txtRealtimeStart);
        txtRealtimeStart.setColumns(10);

        JLabel label = new JLabel("Specify the start time of the timeline in milliseconds");
        springLayout.putConstraint(SpringLayout.NORTH, label, 6, SpringLayout.SOUTH, radioDiscrete);
        springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, lblNewLabel_2);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(label);

        txtDiscreteStart = new JTextField();
        springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 25, SpringLayout.SOUTH, txtDiscreteStart);
        springLayout.putConstraint(SpringLayout.NORTH, txtDiscreteStart, 6, SpringLayout.SOUTH, label);
        springLayout.putConstraint(SpringLayout.WEST, txtDiscreteStart, 0, SpringLayout.WEST, lblNewLabel_2);
        springLayout.putConstraint(SpringLayout.EAST, txtDiscreteStart, 0, SpringLayout.EAST, txtRealtimeMultiplier);
        txtDiscreteStart.setText("0");
        txtDiscreteStart.setHorizontalAlignment(SwingConstants.RIGHT);
        txtDiscreteStart.setEnabled(false);
        txtDiscreteStart.setColumns(10);
        add(txtDiscreteStart);
    }

    public TimelineType getTimelineType()
    {
        if (radioSystem.isSelected())
        {
            return TimelineType.SYSTEM;
        }
        if (radioRealtime.isSelected())
        {
            return TimelineType.REALTIME;
        }
        return TimelineType.DISCRETE;
    }

    public double getMultiplier()
    {
        return Double.parseDouble(txtRealtimeMultiplier.getText());
    }

    public long getStartTime()
    {
        if (getTimelineType() == TimelineType.DISCRETE)
        {
            return Long.parseLong(txtDiscreteStart.getText());
        }
        return Long.parseLong(txtRealtimeStart.getText());
    }

    public void setCancelAction(Action action)
    {
        btnCancel.setAction(action);
    }

    public void setCreateAction(Action action)
    {
        btnCreate.setAction(action);
    }
}
