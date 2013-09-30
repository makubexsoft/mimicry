package com.gc.mimicry.ui.model;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.google.common.base.Preconditions;

public class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer
{
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
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        File file = (File) value;
        if (file.isDirectory())
        {
            if (expanded)
            {
                setIcon(directoryExpandedIcon);
            }
            else
            {
                setIcon(directoryIcon);
            }
        }
        else
        {
            setIcon(fileIcon);
        }
        setText(file.getName());
        return this;
    }
}
