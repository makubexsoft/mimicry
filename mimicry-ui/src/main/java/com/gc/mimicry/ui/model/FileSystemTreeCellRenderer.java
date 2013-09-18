package com.gc.mimicry.ui.model;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.google.common.base.Preconditions;

public class FileSystemTreeCellRenderer implements TreeCellRenderer
{
    private JLabel renderer;
    private Icon directoryIcon;
    private Icon fileIcon;
    private Icon directoryExpandedIcon;

    public FileSystemTreeCellRenderer(Icon directoryIcon, Icon directoryExpanded, Icon fileIcon)
    {
        Preconditions.checkNotNull(directoryIcon);
        Preconditions.checkNotNull(directoryExpanded);
        Preconditions.checkNotNull(fileIcon);

        this.directoryIcon = directoryIcon;
        this.directoryExpandedIcon = directoryExpanded;
        this.fileIcon = fileIcon;

        renderer = new JLabel();
        renderer.setOpaque(false);
        renderer.setBackground(new Color(0, 0, 255, 100));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus)
    {
        File file = (File) value;
        if (file.isDirectory())
        {
            if (expanded)
            {
                renderer.setIcon(directoryExpandedIcon);
            }
            else
            {
                renderer.setIcon(directoryIcon);
            }
        }
        else
        {
            renderer.setIcon(fileIcon);
        }
        renderer.setSize(tree.getWidth(), renderer.getHeight());
        renderer.setOpaque(selected);
        renderer.setText(file.getName());
        return renderer;
    }
}
