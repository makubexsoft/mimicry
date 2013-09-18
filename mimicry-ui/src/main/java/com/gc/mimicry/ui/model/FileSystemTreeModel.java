package com.gc.mimicry.ui.model;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class FileSystemTreeModel extends DefaultTreeModel
{
    private File root;
    private FilenameFilter filter;

    public FileSystemTreeModel(File root, FilenameFilter filter)
    {
        super(new DefaultMutableTreeNode());
        this.root = root;
        this.filter = filter;
    }

    @Override
    public File getRoot()
    {
        return root;
    }

    @Override
    public boolean isLeaf(Object node)
    {
        return ((File) node).isFile();
    }

    @Override
    public int getChildCount(Object parent)
    {
        String[] children = ((File) parent).list(filter);
        if (children == null)
        {
            return 0;
        }
        return children.length;
    }

    @Override
    public File getChild(Object parent, int index)
    {
        String[] children = ((File) parent).list(filter);
        if ((children == null) || (index >= children.length))
        {
            return null;
        }
        return new File((File) parent, children[index]);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        String[] children = ((File) parent).list(filter);
        if (children == null)
        {
            return -1;
        }
        String childname = ((File) child).getName();
        for (int i = 0; i < children.length; i++)
        {
            if (childname.equals(children[i]))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newvalue)
    {
    }
}